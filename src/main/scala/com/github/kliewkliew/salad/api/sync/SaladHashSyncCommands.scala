package com.github.kliewkliew.salad.api.sync


import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.api.sync.RedisHashCommands

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladHashSyncCommands[EK,EV,API] {
  def underlying: API with RedisHashCommands[EK,EV]

  /**
    * Delete a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field to delete.
    * @param keySerde The serde to encode the key and field.
    * @tparam DK The unencoded key type.
    * @return A Boolean indicating success.
    */
  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK])
  : Boolean =
  underlying.hdel(keySerde.serialize(key), keySerde.serialize(field)) == 1

  /**
    * Get a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field for which to get the value.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return An Option of the decoded value.
    */
  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Option[DV] =
  Option.apply(underlying.hget(keySerde.serialize(key), keySerde.serialize(field)))
    .map(valSerde.deserialize)

  /**
    * Set a field-value pair for a hash key.
    * @param key The hash key.
    * @param field The field to set.
    * @param value The value to for the field.
    * @param nx Only set the key if it does not already exist.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Boolean indicating success.
    */
  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Boolean =
  if (nx)
    underlying.hsetnx(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
  else
    underlying.hset(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))

}
