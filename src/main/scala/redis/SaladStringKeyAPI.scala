package redis

import com.lambdaworks.redis.api.async.RedisAsyncCommands
import redis.serde.Serde

import scala.concurrent.Future

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to K.
  * @param commands The lettuce async API to be wrapped.
  * @tparam K The key storage encoding.
  * @tparam V The value storage encoding.
  */
case class SaladStringKeyAPI[K,V](commands: RedisAsyncCommands[K, V])  {
  val api = SaladAPI(commands)

  def get[U](key: String)(implicit keySerde: Serde[String,K], valSerde: Serde[U,V]): Future[Option[U]] =
    api.get[String,U](key)
}
