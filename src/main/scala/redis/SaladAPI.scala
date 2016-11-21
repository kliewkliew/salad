package redis

import com.lambdaworks.redis.SetArgs
import com.lambdaworks.redis.api.async.RedisAsyncCommands

import scala.compat.java8.FutureConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions
import redis.serde.Serde

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @param commands The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
case class SaladAPI[EK, EV](commands: RedisAsyncCommands[EK, EV]) {

  /**
    * Delete a key-value pair from Redis.
    * @param key The key to delete.
    * @param keySerde The serde to encode the key.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
  commands.del(keySerde.serialize(key)).toScala
    .map(_ == 1)

  /**
    * Get a key-value from Redis.
    * @param key The key for which to get the value from Redis.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the value returned by Redis.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Future[Option[DV]] =
  commands.get(keySerde.serialize(key)).toScala
    .map(value => Option.apply(value)
      .map(valSerde.deserialize))

  /**
    * Set a key-value pair in Redis.
    * @param key The key to set.
    * @param value The value for the key.
    * @param ex The expiry time in seconds.
    * @param px The expiry time in milliseconds.
    * @param nx Only set the key if it does not already exist.
    * @param xx Only set the key if it already exist.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to encode the value.
    * @tparam DK The unencoded key type.
    * @tparam DV The unencoded value type.
    * @return A Future indicating success.
    */
  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Future[Boolean] = {
    val args = new SetArgs
    ex.map(args.ex)
    px.map(args.px)
    if (nx) args.nx()
    if (xx) args.xx()

    commands.set(keySerde.serialize(key), valSerde.serialize(value), args).toScala
      .map(_ == "OK")
  }

}
