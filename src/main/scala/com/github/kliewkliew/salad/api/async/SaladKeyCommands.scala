package com.github.kliewkliew.salad.api.async

import FutureConverters._
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.MigrateArgs
import com.lambdaworks.redis.api.async.RedisKeyAsyncCommands

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.util.Try

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladKeyCommands[EK,EV,API] {
  def underlying: API with RedisKeyAsyncCommands[EK,EV]

  /**
    * Delete a key-value pair.
    * @param key The key to delete.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
    Try(underlying.del(keySerde.serialize(key))).toFuture

  /**
    * Set a key's TTL in seconds.
    * @param key The key to expire.
    * @param ex The TTL in seconds.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def expire[DK](key: DK, ex: Long)
                (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
    Try(underlying.expire(keySerde.serialize(key), ex)).toFuture

  /**
    * Atomically transfer one or more keys from a Redis instance to another one.
    * @param host The destination host.
    * @param port The destination port.
    * @param db The destination database.
    * @param timeout The timeout in milliseconds.
    * @param copy Do not remove the key from the local instance.
    * @param replace Replace existing key on the remote instance.
    * @param keys The list of keys to migrate.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def migrate[DK](host: String, keys: List[DK],
                  port: Int = 6379, db: Int = 0, timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[DK,EK])
  : Future[Unit] = {
    val encodedKeys = keys.map(keySerde.serialize).asJava
    val args = MigrateArgs.Builder.keys(encodedKeys)
    if (copy) args.copy()
    if (replace) args.replace()

    Try(underlying.migrate(host, port, db, timeout, args)).toFuture.isOK
  }

  /**
    * Set a key's TTL in milliseconds.
    * @param key The key to expire.
    * @param px The TTL in milliseconds.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def pexpire[DK](key: DK, px: Long)
                 (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
    Try(underlying.pexpire(keySerde.serialize(key), px)).toFuture

  /**
    * Remove the expiry from a key.
    * @param key The key for which to unset expiry.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def persist[DK](key: DK)
                 (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
    Try(underlying.persist(keySerde.serialize(key))).toFuture

}
