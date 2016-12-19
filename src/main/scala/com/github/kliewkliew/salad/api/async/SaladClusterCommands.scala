package com.github.kliewkliew.salad.api.async

import java.net.URI

import FutureConverters._
import com.lambdaworks.redis.RedisURI
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands
import com.lambdaworks.redis.cluster.models.partitions.{ClusterPartitionParser, RedisClusterNode}
import com.lambdaworks.redis.models.role.RedisInstance.Role

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

  /**
    *
    * @see RedisClusterAsyncCommands for javadocs per method.
    * @return Future(Unit) on "OK", else Future.failed(exception)
    */
  def clusterMeet(ip: String, port: Int): Future[Unit] =
    Try(underlying.clusterMeet(ip, port)).toFuture.isOK

  def clusterForget(nodeId: String): Future[Unit] =
    Try(underlying.clusterForget(nodeId)).toFuture.isOK

  def clusterSetSlotNode(slot: Int, nodeId: String): Future[Unit] =
    Try(underlying.clusterSetSlotNode(slot, nodeId)).toFuture.isOK

  def clusterReplicate(nodeId: String): Future[Unit] =
    Try(underlying.clusterReplicate(nodeId)).toFuture.isOK

  def clusterFailover(force: Boolean): Future[Unit] =
    Try(underlying.clusterFailover(force)).toFuture.isOK

  /**
    * Get the information of one node in the cluster.
    * @param uRI
    * @return
    */
  def node(uRI: URI): RedisClusterNode = {
    val node = new RedisClusterNode
    node.setUri(RedisURI.create(uRI.getHost, uRI.getPort))
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
