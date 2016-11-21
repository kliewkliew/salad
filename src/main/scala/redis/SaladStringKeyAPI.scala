package redis

import com.lambdaworks.redis.api.async.RedisAsyncCommands
import redis.serde.Serde

import scala.concurrent.Future

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * The unencoded input key is always a String to be encoded to K.
  * @param commands The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
case class SaladStringKeyAPI[EK,EV](commands: RedisAsyncCommands[EK, EV])  {
  val api = SaladAPI(commands)

  def del(key: String)(implicit keySerde: Serde[String,EK]): Future[Boolean] =
    api.del(key)

  def get[DV](key: String)(implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV]): Future[Option[DV]] =
    api.get[String,DV](key)

  def set[DV](key: String, value: DV, ex: Option[Long] = None, px: Option[Long] = None, nx: Boolean = false, xx: Boolean = false)
             (implicit keySerde: Serde[String,EK], valSerde: Serde[DV,EV]): Future[Boolean] =
    api.set[String,DV](key, value)
}
