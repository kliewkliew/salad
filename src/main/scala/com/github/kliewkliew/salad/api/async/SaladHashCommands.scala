package com.github.kliewkliew.salad.api.async

import FutureConverters._
import com.github.kliewkliew.salad.api.TryConverters._

import com.github.kliewkliew.salad.api.logging.SaladHashCommandLogger
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.async.RedisHashAsyncCommands

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladHashCommands[EK,EV,API] {
  def underlying: API with RedisHashAsyncCommands[EK,EV]

  /**
    * Delete a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field to delete.
    * @param keySerde The serde to encode the key and field.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = Try(underlying.hdel(keySerde.serialize(key), keySerde.serialize(field))).toFuture
    resultF.onComplete(result => SaladHashCommandLogger.hdel(key, field)(result))
    resultF
  }

  /**
    * Get a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field for which to get the value.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)

  : Future[Option[DV]] = {
    val resultF = Try(underlying.hget(keySerde.serialize(key), keySerde.serialize(field))).toFuture
      .map(value => Option.apply(value)
        .map(valSerde.deserialize))
    resultF.onComplete(result => SaladHashCommandLogger.hget(key, field)(result))
    resultF
  }

  /**
    * Set a field-value pair for a hash key.
    * @param key The hash key.
    * @param field The field to set.
    * @param value The value to for the field.
    * @param nx Only set the key if it does not already exist.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future indicating success.
    */
  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = Try(
      if (nx)
        underlying.hsetnx(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
      else
        underlying.hset(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
    ).toFuture
    resultF.onComplete(result => SaladHashCommandLogger.hset(key, field, value, nx)(result))
    resultF
  }

}
