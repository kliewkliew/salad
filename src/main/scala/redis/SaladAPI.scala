package redis

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
    * Get a value from Redis.
    * @param key The key for which to get the value from Redis.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the value returned by Redis.
    * @tparam DK The unencoded key/parameter type.
    * @tparam DV The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def get[DK,DV](key: DK)(implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV]): Future[Option[DV]] =
    commands.get(keySerde.serialize(key)).toScala
      .map(value => Option.apply(value)
        .map(valSerde.deserialize))

  /**
    * Set a key-value pair in Redis.
    * @param key The key to set.
    * @param value The value for the key.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to encode the value.
    * @tparam DK The unencoded key/parameter type.
    * @tparam DV The unencoded value type.
    * @return A Future indicating success.
    */
  def set[DK,DV](key: DK, value: DV)(implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV]): Future[Boolean] =
    commands.set(keySerde.serialize(key), valSerde.serialize(value)).toScala
      .map(_ == "OK")

}

/**
  * Convert from RedisFuture holding a Java type to a Scala Future and type
  * *//*
object FutureConverters {
  implicit def RedisFutureToScalaFutureNumber[J <: Number,S](rF: RedisFuture[J]): Future[S] =
    rF.asInstanceOf[ListenableFuture[S]]
      .asScala
  implicit def RedisFutureToScalaFutureBoolean(rF: RedisFuture[java.lang.Boolean]): Future[Boolean] =
    rF.asInstanceOf[ListenableFuture[Boolean]]
      .asScala
  /*
  implicit def RedisFutureToScalaFutureString[V](rF: RedisFuture[String])(implicit convert: Serde[V]): Future[Option[V]] =
    rF.asInstanceOf[ListenableFuture[String]]
      .asScala
      .map(value => Option.apply(convert.deserialize(ByteString(value))))*/
}*/