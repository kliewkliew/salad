package com.github.kliewkliew.salad.api

import com.github.kliewkliew.salad.serde.Serde

import scala.concurrent.{ExecutionContext, Future}

/**
  * Interface with Redis hash data structures.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladHashCommands[EK,EV,API] {
  /**
    * Delete a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field to delete.
    * @param keySerde The serde to encode the key and field.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean]

  /**
    * Get a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field for which to get the value.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]]

  /**
    * Set a field-value pair for a hash key.
    * @param key The hash key.
    * @param field The field to set.
    * @param value The value to for the field.
    * @param nx Only set the key if it does not already exist.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @param executionContext The thread dispatcher.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future indicating success.
    */
  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean]

}
