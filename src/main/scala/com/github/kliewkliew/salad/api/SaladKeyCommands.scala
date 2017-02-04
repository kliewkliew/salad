package com.github.kliewkliew.salad.api

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI

import scala.concurrent.{ExecutionContext, Future}

/**
  * Interface with Redis keys.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladKeyCommands[EK,EV,API] {
  /**
    * Delete a key-value pair.
    * @param key The key to delete.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean]

  /**
    * Set a key's TTL in seconds.
    * @param key The key to expire.
    * @param ex The TTL in seconds.
    * @param keySerde The serde to encode the key.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def expire[DK](key: DK, ex: Long)
                (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean]

  /**
    * Atomically transfer one or more keys from a Redis instance to another one.
    * @param redisURI The destination URI.
    * @param timeout The timeout in milliseconds.
    * @param copy Do not remove the key from the local instance.
    * @param replace Replace existing key on the remote instance.
    * @param keys The list of keys to migrate.
    * @param keySerde The serde to encode the key.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def migrate[DK](redisURI: RedisURI, keys: List[DK], timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Unit]

  /**
    * Set a key's TTL in milliseconds.
    * @param key The key to expire.
    * @param px The TTL in milliseconds.
    * @param keySerde The serde to encode the key.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def pexpire[DK](key: DK, px: Long)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean]

  /**
    * Remove the expiry from a key.
    * @param key The key for which to unset expiry.
    * @param keySerde The serde to encode the key.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def persist[DK](key: DK)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean]

}
