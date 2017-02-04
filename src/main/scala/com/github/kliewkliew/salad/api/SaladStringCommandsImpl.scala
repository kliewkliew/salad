package com.github.kliewkliew.salad.api

import ImplicitFutureConverters._

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.SetArgs
import com.lambdaworks.redis.api.async.RedisStringAsyncCommands

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Wrap the lettuce API to implement SaladStringCommands.
  * @see SaladStringCommands for javadocs.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladStringCommandsImpl[EK,EV,API] extends SaladStringCommands[EK,EV,API] {
  def underlying: API with RedisStringAsyncCommands[EK,EV]

  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    Try(underlying.get(keySerde.serialize(key))).toFuture
      .map(value => Option.apply(value)
        .map(valSerde.deserialize))

  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Unit] = {
    val args = new SetArgs
    ex.map(args.ex)
    px.map(args.px)
    if (nx) args.nx()
    if (xx) args.xx()

    Try(underlying.set(keySerde.serialize(key), valSerde.serialize(value), args)).toFuture.isOK
  }

}
