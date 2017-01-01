package com.github.kliewkliew.salad.api.async

import java.net.InetAddress

import FutureConverters._
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands
import com.lambdaworks.redis.cluster.models.partitions.{ClusterPartitionParser, RedisClusterNode}
import com.lambdaworks.redis.models.role.RedisInstance.Role
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  *
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladClusterCommands[EK,EV,API] {
  def underlying: API with RedisClusterAsyncCommands[EK,EV]

  private val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Invoke the underlying methods with additional logging.
    *
    * @see RedisClusterAsyncCommands for javadocs per method.
    * @return Future(Unit) on "OK", else Future.failed(exception)
    */
  def clusterMeet(redisURI: RedisURI)
                 (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val met = Try(underlying.clusterMeet(
      InetAddress.getByName(redisURI.getHost).getHostAddress, // Hostname will not work; use the IP address
      redisURI.getPort)).toFuture.isOK
    met.onSuccess { case _ => logger.info(s"Added node to cluser: $redisURI") }
    met.onFailure { case e => logger.warn(s"Failed to add node to cluster: $redisURI", e) }
    met
  }

  def clusterForget(nodeId: String)
                   (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val forgot = Try(underlying.clusterForget(nodeId)).toFuture.isOK
    clusterMyId.map { executorId =>
      forgot.onSuccess { case _ => logger.info(s"Forgot $nodeId from $executorId") }
      forgot.onFailure { case e => logger.warn(s"Failed to forget $nodeId from $executorId", e) }
    }
    forgot
  }

  def clusterSetSlotNode(slot: Int, nodeId: String)
                        (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val sat = Try(underlying.clusterSetSlotNode(slot, nodeId)).toFuture.isOK
    sat.onSuccess { case _ => logger.trace(s"Assigned slot $slot to $nodeId") }
    sat.onFailure { case e => logger.trace(s"Failed to assign slot $slot to $nodeId", e) }
    sat
  }

  def clusterSetSlotStable(slot: Int)
                          (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val sat = Try(underlying.clusterSetSlotStable(slot)).toFuture.isOK
    sat.onSuccess { case _ => logger.trace(s"Stabilized slot $slot") }
    sat.onFailure { case e => logger.trace(s"Failed to stabilize slot $slot", e) }
    sat
  }

  def clusterSetSlotMigrating(slot: Int, nodeId: String)
                             (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val sat = Try(underlying.clusterSetSlotMigrating(slot, nodeId)).toFuture.isOK
    sat.onSuccess { case _ => logger.trace(s"Migrating slot $slot to $nodeId") }
    sat.onFailure { case e => logger.trace(s"Failed to migrate slot $slot to $nodeId", e) }
    sat
  }

  def clusterSetSlotImporting(slot: Int, nodeId: String)
                             (implicit executionContext: ExecutionContext)
  : Future[Unit] = {
    val sat = Try(underlying.clusterSetSlotImporting(slot, nodeId)).toFuture.isOK
    sat.onSuccess { case _ => logger.trace(s"Importing slot $slot from $nodeId") }
    sat.onFailure { case e => logger.trace(s"Failed to import slot $slot from $nodeId", e) }
    sat
  }

  def clusterGetKeysInSlot[DK](slot: Int, count: Int)
                          (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[mutable.Buffer[DK]] = {
    val encodedKeys = Try(underlying.clusterGetKeysInSlot(slot, count)).toFuture
    val decodedKeys = encodedKeys.map { keyList => keyList.asScala.map { key =>
      keySerde.deserialize(key)
    }}
    decodedKeys.onSuccess { case result => logger.trace(s"Keys for slot $slot are $result") }
    decodedKeys.onFailure { case e => logger.error(s"Failed to get keys for slot $slot", e) }
    decodedKeys
  }

  def clusterCountKeysInSlot(slot: Int)
                            (implicit executionContext: ExecutionContext)
  : Future[Long] = {
    val count = Try(underlying.clusterCountKeysInSlot(slot)).toFuture
    count.onSuccess { case result => logger.trace(s"$result keys in slot $slot") }
    count.onFailure { case e => logger.trace(s"Failed to count keys in slot $slot", e) }
    count
  }

  def clusterReplicate(nodeId: String)
                      (implicit executionContext: ExecutionContext)
  : Future[Unit] =
    Try(underlying.clusterMyId()).toFuture.flatMap { replicaId =>
      val replicated = Try(underlying.clusterReplicate(nodeId)).toFuture.isOK
      replicated.onSuccess { case _ => logger.info(s"$replicaId replicates $nodeId") }
      replicated.onFailure { case e => logger.warn(s"Failed to set $replicaId to replicate $nodeId", e) }
      replicated
    }

  def clusterFailover(force: Boolean)
                     (implicit executionContext: ExecutionContext)
  : Future[Unit] =
    clusterMyId.flatMap { newMaster =>
      val failover = Try(underlying.clusterFailover(force)).toFuture.isOK
      failover.onSuccess { case _ => logger.info(s"Failover to $newMaster") }
      failover.onFailure { case e => logger.warn(s"Failed to failover to $newMaster", e) }
      failover
    }

  def clusterReset(hard: Boolean)
                  (implicit executionContext: ExecutionContext)
  : Future[Unit] =
    clusterMyId.flatMap { oldId =>
      val reset = Try(underlying.clusterReset(hard)).toFuture.isOK
      reset.onSuccess { case _ => logger.info(s"Reset node: $oldId") }
      reset.onFailure { case e => logger.warn(s"Failed to reset node: $oldId", e) }
      reset
    }

  def clusterMyId: Future[String] =
    Try(underlying.clusterMyId).toFuture

  /**
    * Get a list of nodes in the cluster.
    * @return
    */
  def clusterNodes(implicit executionContext: ExecutionContext): Future[mutable.Buffer[RedisClusterNode]] =
    underlying.clusterNodes.map(ClusterPartitionParser.parse).map(_.getPartitions.asScala)
  def masterNodes(implicit executionContext: ExecutionContext): Future[mutable.Buffer[RedisClusterNode]] =
    clusterNodes.map(_.filter(Role.MASTER == _.getRole))
  def masterNodes(amongNodes: mutable.Buffer[RedisClusterNode]): mutable.Buffer[RedisClusterNode] =
    amongNodes.filter(Role.MASTER == _.getRole)
  def slaveNodes(implicit executionContext: ExecutionContext): Future[mutable.Buffer[RedisClusterNode]] =
    clusterNodes.map(_.filter(Role.SLAVE == _.getRole))
  def slaveNodes(amongNodes: mutable.Buffer[RedisClusterNode]): mutable.Buffer[RedisClusterNode] =
    amongNodes.filter(Role.SLAVE == _.getRole)

}
