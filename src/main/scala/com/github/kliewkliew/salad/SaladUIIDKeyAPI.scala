package com.github.kliewkliew.salad

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.async._

import scala.concurrent.Future

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to a byte-array.
  * The value is encoded using the implicit serde.
  * @see SaladAPI for javadocs per method.
  * @param underlying The lettuce async API to be wrapped.
  */
case class SaladUIIDKeyAPI[API]
(underlying: API
  with RedisHashAsyncCommands[Array[Byte],Array[Byte]]
  with RedisKeyAsyncCommands[Array[Byte],Array[Byte]]
  with RedisStringAsyncCommands[Array[Byte],Array[Byte]]
) {
  val api = SaladStringKeyAPI(underlying)
  import com.github.kliewkliew.salad.serde.ByteArraySerdes.stringSerde

  def del(key: String)
  : Future[Boolean] =
    api.del(key)

  def expire(key: String, ex: Long)
  : Future[Boolean] =
    api.expire(key, ex)

  def pexpire(key: String, px: Long)
  : Future[Boolean] =
    api.pexpire(key, px)

  def persist(key: String)
  : Future[Boolean] =
    api.persist(key)

  def get[DV](key: String)
             (implicit valSerde: Serde[DV,Array[Byte]])
  : Future[Option[DV]] =
    api.get(key)(stringSerde, valSerde)

  def set[DV](key: String, value: DV,
              ex: Option[Long] = None, px: Option[Long] = None,
              nx: Boolean = false, xx: Boolean = false)
             (implicit valSerde: Serde[DV,Array[Byte]])
  : Future[Boolean] =
    api.set(key, value, ex, px, nx, xx)(stringSerde, valSerde)

  def hdel(key: String, field: String)
  : Future[Boolean] =
    api.hdel(key, field)(stringSerde)

  def hget[DV](key: String, field: String)
              (implicit valSerde: Serde[DV,Array[Byte]])
  : Future[Option[DV]] =
    api.hget(key, field)(stringSerde, valSerde)

  def hset[DV](key: String, field: String, value: DV,
               nx: Boolean = false)
              (implicit valSerde: Serde[DV,Array[Byte]])
  : Future[Boolean] =
    api.hset(key, field, value, nx)(stringSerde, valSerde)

}
