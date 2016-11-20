package redis

import com.gilt.gfc.guava.future.FutureConverters._
import com.google.common.util.concurrent.ListenableFuture
import com.lambdaworks.redis.RedisFuture
import com.lambdaworks.redis.api.async.RedisAsyncCommands

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import redis.serde.Serde

import FutureConverters._

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @param commands The lettuce async API to be wrapped.
  * @tparam K The key storage encoding.
  * @tparam V The value storage encoding.
  */
case class SaladAPI[K, V](commands: RedisAsyncCommands[K, V]) {

  /**
    * Get a value from Redis.
    * @param key The key for which to get the value from Redis.
    * @param keySerde The serde to encode the key.
    * @param valSerde The serde to decode the value returned by Redis.
    * @tparam J The unencoded key/parameter type.
    * @tparam U The decoded value type.
    * @return A Future containing an Option of the decoded value.
    */
  def get[J,U](key: J)(implicit keySerde: Serde[J,K], valSerde: Serde[U,V]): Future[Option[U]] =
    commands.get(keySerde.serialize(key))
    .asInstanceOf[ListenableFuture[V]]
    .asScala
    .map(value => Option.apply(value).map(valSerde.deserialize))

}

/**
  * Convert from RedisFuture holding a Java type to a Scala Future and type
  * */
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
}