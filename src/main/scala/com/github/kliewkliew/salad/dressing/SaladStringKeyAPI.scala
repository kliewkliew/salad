package com.github.kliewkliew.salad.dressing

import com.github.kliewkliew.salad.api.{SaladHashCommands, SaladKeyCommands, SaladStringCommands}
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI

import scala.concurrent.{ExecutionContext, Future}

/**
  * The unencoded input key is always a String to be encoded to EK.
  * @see the composing traits for javadocs.
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
class SaladStringKeyAPI[EK,EV,SALAD,LETTUCE]
(val underlying: SALAD
  with SaladHashCommands[EK,EV,LETTUCE]
  with SaladKeyCommands[EK,EV,LETTUCE]
  with SaladStringCommands[EK,EV,LETTUCE]) {

  def hdel(key: String, field: String)
              (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.hdel(key, field)

  def hget[DV](key: String, field: String)
                 (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)

  : Future[Option[DV]] =
    underlying.hget(key, field)

  def hset[DV](key: String, field: String, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.hset(key, field, value)

  def del(key: String)
             (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.del(key)

  def expire(key: String, ex: Long)
                (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.expire(key, ex)

  def migrate(redisURI: RedisURI, keys: List[String], timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Unit] =
    underlying.migrate(redisURI, keys, timeout, copy, replace)

  def pexpire(key: String, px: Long)
                 (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.pexpire(key, px)

  def persist(key: String)
                 (implicit keySerde: Serde[String,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    underlying.persist(key)

  def get[DV](key: String)
                (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    underlying.get(key)

  def set[DV](key: String, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Unit] =
    underlying.set(key, value, ex, px, nx, xx)

}
