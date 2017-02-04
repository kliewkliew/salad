package com.github.kliewkliew.salad.api

import ImplicitFutureConverters._

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.{MigrateArgs, RedisURI}
import com.lambdaworks.redis.api.async.RedisKeyAsyncCommands

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Wrap the lettuce API to implement SaladKeyCommands.
  * @see SaladKeyCommands for javadocs.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladKeyCommandsImpl[EK,EV,API] extends SaladKeyCommands[EK,EV,API] {
  def underlying: API with RedisKeyAsyncCommands[EK,EV]

  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(underlying.del(keySerde.serialize(key))).toFuture

  def expire[DK](key: DK, ex: Long)
                (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(underlying.expire(keySerde.serialize(key), ex)).toFuture

  def migrate[DK](redisURI: RedisURI, keys: List[DK], timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Unit] = {
    val host = redisURI.getHost
    val port = Option.apply(redisURI.getPort).getOrElse(6379)
    val db = Option.apply(redisURI.getDatabase).getOrElse(0)
    val encodedKeys = keys.map(keySerde.serialize).asJava
    val args = MigrateArgs.Builder.keys(encodedKeys)
    if (copy) args.copy()
    if (replace) args.replace()

    Try(underlying.migrate(host, port, db, timeout, args)).toFuture.isOK
  }

  def pexpire[DK](key: DK, px: Long)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(underlying.pexpire(keySerde.serialize(key), px)).toFuture

  def persist[DK](key: DK)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(underlying.persist(keySerde.serialize(key))).toFuture

}
