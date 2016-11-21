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
    * Delete a key-value pair.
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
  commands.expire(keySerde.serialize(key), ex).toScala
    .map(_ == true)

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
  commands.pexpire(keySerde.serialize(key), px).toScala
    .map(_ == true)

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
  commands.persist(keySerde.serialize(key)).toScala
    .map(_ == true)

  /**
    * Get a key-value.
    * @param key The key for which to get the value.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the returned value.
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
      .map(_ == "OK")// This is necessary to convert java.lang.Boolean to scala.Boolean
  }

  /**
    * Delete a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field to delete.
    * @param keySerde The serde to encode the key and field.
    * @tparam DK The unencoded key type.
    * @return A Future indicating success.
    */
  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK])
  : Future[Boolean] =
  commands.hdel(keySerde.serialize(key), keySerde.serialize(field)).toScala
    .map(_ == 1)

  /**
    * Get a field-value pair of a hash key.
    * @param key The hash key.
    * @param field The field for which to get the value.
    * @param keySerde The serde to encode the key and field.
    * @param valSerde The serde to decode the returned value.
    * @tparam DK The unencoded key type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Future[Option[DV]] =
  commands.hget(keySerde.serialize(key), keySerde.serialize(field)).toScala
    .map(value => Option.apply(value)
      .map(valSerde.deserialize))

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
    * @return A Future indicating success.
    */
  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV])
  : Future[Boolean] = {
    if (nx)
      commands.hset(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
    else
      commands.hsetnx(keySerde.serialize(key), keySerde.serialize(field), valSerde.serialize(value))
  }.toScala
    .map(_ == true)// This is necessary to convert java.lang.Boolean to scala.Boolean

}
