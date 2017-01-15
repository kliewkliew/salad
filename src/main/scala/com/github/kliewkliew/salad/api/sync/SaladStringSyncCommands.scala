package com.github.kliewkliew.salad.api.sync

import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.SetArgs
import com.lambdaworks.redis.api.sync.RedisStringCommands

import scala.util.Try

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
trait SaladStringSyncCommands[EK,EV,API] {
  def underlying: API with RedisStringCommands[EK,EV]

  /**
    * Get a key-value.
    * @param key The key for which to get the value.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return An Option of the decoded value.
    */
  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Try[Option[DV]] =
    Try(
      Option.apply(underlying.get(keySerde.serialize(key)))
        .map(valSerde.deserialize))


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
    * @tparam DK The unencoded key type.
    * @tparam DV The unencoded value type.
    * @return A Try indicating success.
    */
  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Try[Unit] = {
    val args = new SetArgs
    ex.map(args.ex)
    px.map(args.px)
    if (nx) args.nx()
    if (xx) args.xx()

    Try(underlying.set(keySerde.serialize(key), valSerde.serialize(value), args)).map(_ == "OK")
  }

}
