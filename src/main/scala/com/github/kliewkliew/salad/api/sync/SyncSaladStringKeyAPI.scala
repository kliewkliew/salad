package com.github.kliewkliew.salad.api.sync

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.sync._

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to EK.
  * @see SyncSaladAPI for javadocs per method.
  * @param underlying The lettuce sync API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
case class SyncSaladStringKeyAPI[EK,EV,API]
(underlying: API
  with RedisHashCommands[EK,EV]
  with RedisKeyCommands[EK,EV]
  with RedisStringCommands[EK,EV]
) {
  val api = SyncSaladAPI(underlying)

  def del(key: String)
         (implicit keySerde: Serde[String,EK])
  : Boolean =
    api.del(key)

  def expire(key: String, ex: Long)
            (implicit keySerde: Serde[String,EK])
  : Boolean =
    api.expire(key, ex)

  def pexpire(key: String, px: Long)
             (implicit keySerde: Serde[String,EK])
  : Boolean =
    api.pexpire(key, px)

  def persist(key: String)
             (implicit keySerde: Serde[String,EK])
  : Boolean =
    api.persist(key)

  def get[DV](key: String)
             (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV])
  : Option[DV] =
    api.get(key)

  def set[DV](key: String, value: DV,
              ex: Option[Long] = None, px: Option[Long] = None,
              nx: Boolean = false, xx: Boolean = false)
             (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV])
  : Boolean =
    api.set(key, value, ex, px, nx, xx)

  def hdel(key: String, field: String)
          (implicit keySerde: Serde[String,EK])
  : Boolean =
    api.hdel(key, field)

  def hget[DV](key: String, field: String)
              (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV])
  : Option[DV] =
    api.hget(key, field)

  def hset[DV](key: String, field: String, value: DV,
               nx: Boolean = false)
              (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV])
  : Boolean =
    api.hset(key, field, value, nx)

}
