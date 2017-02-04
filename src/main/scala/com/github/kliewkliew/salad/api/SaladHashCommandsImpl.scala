package com.github.kliewkliew.salad.api

import ImplicitFutureConverters._

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.async.RedisHashAsyncCommands

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Wrap the lettuce API to implement SaladHashCommands.
  * @see SaladHashCommands for javadocs.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladHashCommandsImpl[EK,EV,API] extends SaladHashCommands[EK,EV,API] {
  def underlying: API with RedisHashAsyncCommands[EK,EV]

  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(underlying.hdel(keySerde.serialize(key), keySerde.serialize(field))).toFuture

  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)

  : Future[Option[DV]] =
    Try(underlying.hget(keySerde.serialize(key), keySerde.serialize(field))).toFuture
      .map(value => Option.apply(value)
        .map(valSerde.deserialize))

  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean] =
    Try(
      if (nx)
        underlying.hsetnx(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
      else
        underlying.hset(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
    ).toFuture

}
