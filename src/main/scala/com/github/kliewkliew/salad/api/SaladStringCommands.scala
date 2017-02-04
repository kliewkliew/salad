package com.github.kliewkliew.salad.api

import com.github.kliewkliew.salad.serde.Serde

import scala.concurrent.{ExecutionContext, Future}

/**
  * Interface with Redis key-value data structures.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladStringCommands[EK,EV,API] {
  /**
    * Get a key-value.
    * @param key The key for which to get the value.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the returned value.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]]

  /**
    * Set a key-value pair.
    * @param key The key to set.
    * @param value The value for the key.
    * @param ex TTL in seconds.
    * @param px TTL in milliseconds.
    * @param nx Only set the key if it does not already exist.
    * @param xx Only set the key if it already exist.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to encode the value.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @tparam DV The unencoded value type.
    * @return A Future indicating success.
    */
  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Unit]

}
