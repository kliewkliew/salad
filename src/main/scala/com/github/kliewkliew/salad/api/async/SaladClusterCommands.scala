package com.github.kliewkliew.salad.api.async

import FutureConverters._

import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands

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
}
