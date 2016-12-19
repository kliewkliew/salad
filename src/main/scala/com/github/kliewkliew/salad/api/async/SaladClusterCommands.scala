package com.github.kliewkliew.salad.api.async

import FutureConverters._
import com.lambdaworks.redis.RedisURI
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands
import com.lambdaworks.redis.cluster.models.partitions.{ClusterPartitionParser, RedisClusterNode}
import com.lambdaworks.redis.models.role.RedisInstance.Role
import org.slf4j.LoggerFactory

import collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

  val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Invoke the underlying methods with additional logging.
    *
    * @see RedisClusterAsyncCommands for javadocs per method.
    * @return Future(Unit) on "OK", else Future.failed(exception)
    */
  def clusterMeet(redisURI: RedisURI): Future[Unit] = {
    val met = Try(underlying.clusterMeet(redisURI.getHost, redisURI.getPort)).toFuture.isOK
    met.onSuccess { case result => logger.info(s"Added node to cluser: $redisURI") }
    met.onFailure { case e => logger.warn(s"Failed to add node to cluster: $redisURI", e) }
    met
  }

  def clusterForget(nodeId: String): Future[Unit] = {
    val forgot = Try(underlying.clusterForget(nodeId)).toFuture.isOK
    forgot.onSuccess { case result => logger.info(s"Remove node from cluser: $nodeId") }
    forgot.onFailure { case e => logger.warn(s"Failed to remove node from cluster: $nodeId", e) }
    forgot
  }

  def clusterSetSlotNode(slot: Int, nodeId: String): Future[Unit] = {
    val sat = Try(underlying.clusterSetSlotNode(slot, nodeId)).toFuture.isOK
    sat.onSuccess { case result => logger.trace(s"Set slot $slot to node $nodeId") }
    sat.onFailure { case e => logger.warn(s"Failed to set slot $slot to node $nodeId", e) }
    sat
  }

  def clusterReplicate(nodeId: String): Future[Unit] =
    Try(underlying.clusterMyId()).toFuture.flatMap { replicaId =>
      val replicated = Try(underlying.clusterReplicate(nodeId)).toFuture.isOK
      replicated.onSuccess { case result => logger.info(s"$replicaId replicates $nodeId") }
      replicated.onFailure { case e => logger.warn(s"Failed to add $replicaId as a slave replicating $nodeId", e) }
      replicated
    }

  def clusterFailover(force: Boolean): Future[Unit] =
    Try(underlying.clusterMyId()).toFuture.flatMap { newMaster =>
      val failover = Try(underlying.clusterFailover(force)).toFuture.isOK
      failover.onSuccess { case result => logger.info(s"Failover to $newMaster") }
      failover.onFailure { case e => logger.warn(s"Failed to failover to $newMaster", e) }
      failover
    }

  /**
    * Get the information of one node in the cluster.
    * @param redisURI
    * @return
    */
  def getNode(redisURI: RedisURI): RedisClusterNode = {
    val node = new RedisClusterNode
    node.setUri(redisURI)
    node
  }

  /**
    * Get a list of nodes in the cluster.
    * @return
    */
  def clusterNodes: Future[mutable.Buffer[RedisClusterNode]] =
    underlying.clusterNodes.map(ClusterPartitionParser.parse).map(_.getPartitions.asScala)
  def masterNodes: Future[mutable.Buffer[RedisClusterNode]] =
    clusterNodes.map(_.filter(Role.MASTER == _.getRole))
  def masterNodes(amongNodes: mutable.Buffer[RedisClusterNode]): mutable.Buffer[RedisClusterNode] =
    amongNodes.filter(Role.MASTER == _.getRole)
  def slaveNodes: Future[mutable.Buffer[RedisClusterNode]] =
    clusterNodes.map(_.filter(Role.SLAVE == _.getRole))
  def slaveNodes(amongNodes: mutable.Buffer[RedisClusterNode]): mutable.Buffer[RedisClusterNode] =
    amongNodes.filter(Role.SLAVE == _.getRole)

}
