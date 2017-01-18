package com.github.kliewkliew.salad.api.sync

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.sync._

import scala.util.Try

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to a byte-array.
  * The value is encoded using the implicit serde.
  * @see SyncSaladAPI for javadocs per method.
  * @param underlying The lettuce sync API to be wrapped.
  */
case class SyncSaladUIIDKeyAPI[API]
(underlying: API
  with RedisHashCommands[Array[Byte],Array[Byte]]
  with RedisKeyCommands[Array[Byte],Array[Byte]]
  with RedisStringCommands[Array[Byte],Array[Byte]]
) {
  val api = SyncSaladStringKeyAPI(underlying)
  import com.github.kliewkliew.salad.serde.ByteArraySerdes.stringSerde

  def del(key: String)
  : Try[Boolean] =
    api.del(key)

  def expire(key: String, ex: Long)
  : Try[Boolean] =
    api.expire(key, ex)

  def pexpire(key: String, px: Long)
  : Try[Boolean] =
    api.pexpire(key, px)

  def persist(key: String)
  : Try[Boolean] =
    api.persist(key)

  def get[DV](key: String)
             (implicit valSerde: Serde[DV,Array[Byte]])
  : Try[Option[DV]] =
    api.get(key)(stringSerde, valSerde)

  def set[DV](key: String, value: DV,
              ex: Option[Long] = None, px: Option[Long] = None,
              nx: Boolean = false, xx: Boolean = false)
             (implicit valSerde: Serde[DV,Array[Byte]])
  : Try[Unit] =
    api.set(key, value, ex, px, nx, xx)(stringSerde, valSerde)

  def hdel(key: String, field: String)
  : Try[Boolean] =
    api.hdel(key, field)(stringSerde)

  def hget[DV](key: String, field: String)
              (implicit valSerde: Serde[DV,Array[Byte]])
  : Try[Option[DV]] =
    api.hget(key, field)(stringSerde, valSerde)

  def hset[DV](key: String, field: String, value: DV,
               nx: Boolean = false)
              (implicit valSerde: Serde[DV,Array[Byte]])
  : Try[Boolean] =
    api.hset(key, field, value, nx)(stringSerde, valSerde)

}
