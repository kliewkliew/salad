package com.github.kliewkliew.salad.api.async

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.async._
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands

import scala.concurrent.{ExecutionContext, Future}

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to a byte-array.
  * The value is encoded using the implicit serde.
  * @see AsyncSaladAPI for javadocs per method.
  * @param underlying The lettuce async API to be wrapped.
  */
case class AsyncSaladUIIDKeyAPI[API]
(underlying: API
  with RedisClusterAsyncCommands[Array[Byte],Array[Byte]]
  with RedisHashAsyncCommands[Array[Byte],Array[Byte]]
  with RedisKeyAsyncCommands[Array[Byte],Array[Byte]]
  with RedisStringAsyncCommands[Array[Byte],Array[Byte]]
) {
  val api = AsyncSaladStringKeyAPI(underlying)
  import com.github.kliewkliew.salad.serde.ByteArraySerdes.stringSerde

  def del(key: String)
         (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    api.del(key)

  def expire(key: String, ex: Long)
            (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    api.expire(key, ex)

  def pexpire(key: String, px: Long)
             (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    api.pexpire(key, px)

  def persist(key: String)
             (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    api.persist(key)

  def get[DV](key: String)
             (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    api.get(key)(stringSerde, valSerde, executionContext)

  def set[DV](key: String, value: DV,
              ex: Option[Long] = None, px: Option[Long] = None,
              nx: Boolean = false, xx: Boolean = false)
             (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Unit] =
    api.set(key, value, ex, px, nx, xx)(stringSerde, valSerde, executionContext)

  def hdel(key: String, field: String)
          (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    api.hdel(key, field)(stringSerde, executionContext)

  def hget[DV](key: String, field: String)
              (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    api.hget(key, field)(stringSerde, valSerde, executionContext)

  def hset[DV](key: String, field: String, value: DV,
               nx: Boolean = false)
              (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Boolean] =
    api.hset(key, field, value, nx)(stringSerde, valSerde, executionContext)

}
