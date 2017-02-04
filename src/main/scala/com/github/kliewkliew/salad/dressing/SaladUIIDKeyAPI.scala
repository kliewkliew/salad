package com.github.kliewkliew.salad.dressing

import com.github.kliewkliew.salad.api.{SaladHashCommands, SaladKeyCommands, SaladStringCommands}
import com.github.kliewkliew.salad.serde.ByteArraySerdes.stringSerde
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI

import scala.concurrent.{ExecutionContext, Future}

/**
  * The unencoded input key is always a String to be encoded to a byte-array.
  * The value is encoded using the implicit serde.
  * @see the composing traits for javadocs.
  * @param underlying The Salad API to augment.
  * @tparam SALAD The Salad API.
  * @tparam LETTUCE The Lettuce API.
  */
class SaladUIIDKeyAPI[SALAD,LETTUCE]
(val underlying: SALAD
  with SaladHashCommands[Array[Byte],Array[Byte],LETTUCE]
  with SaladKeyCommands[Array[Byte],Array[Byte],LETTUCE]
  with SaladStringCommands[Array[Byte],Array[Byte],LETTUCE]) {

  def hdel(key: String, field: String)
          (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.hdel(key, field)(stringSerde, executionContext)

  def hget[DV](key: String, field: String)
              (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)

  : Future[Option[DV]] =
    underlying.hget(key, field)(stringSerde, valSerde, executionContext)

  def hset[DV](key: String, field: String, value: DV,
               nx: Boolean = false)
              (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.hset(key, field, value)(stringSerde, valSerde, executionContext)

  def del(key: String)
         (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.del(key)(stringSerde, executionContext)

  def expire(key: String, ex: Long)
            (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.expire(key, ex)(stringSerde, executionContext)

  def migrate(redisURI: RedisURI, keys: List[String], timeout: Long = 5000,
              copy: Boolean = false, replace: Boolean = false)
             (implicit executionContext: ExecutionContext)
  : Future[Unit] =
    underlying.migrate(redisURI, keys, timeout, copy, replace)(stringSerde, executionContext)

  def pexpire(key: String, px: Long)
             (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.pexpire(key, px)(stringSerde, executionContext)

  def persist(key: String)
             (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.persist(key)(stringSerde, executionContext)

  def get[DV](key: String)
             (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    underlying.get(key)(stringSerde, valSerde, executionContext)

  def set[DV](key: String, value: DV,
              ex: Option[Long] = None, px: Option[Long] = None,
              nx: Boolean = false, xx: Boolean = false)
             (implicit valSerde: Serde[DV,Array[Byte]], executionContext: ExecutionContext)
  : Future[Unit] =
    underlying.set(key, value, ex, px, nx, xx)(stringSerde, valSerde, executionContext)

}
