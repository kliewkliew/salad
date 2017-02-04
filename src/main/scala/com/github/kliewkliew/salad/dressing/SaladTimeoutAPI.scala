package com.github.kliewkliew.salad.dressing

import com.github.kliewkliew.salad.api.{SaladHashCommands, SaladKeyCommands, SaladStringCommands}
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Timeout {
  val default: FiniteDuration =
    Try(ConfigFactory.load().getConfig("salad")).toOption
      .map(_.getInt("request-timeout")).getOrElse(1000).milliseconds
}

/**
  * Decorate a Salad API by throwing an exception after requests reach a timeout.
  * This wrapper is lightweight so you can instantiate multiple to use different timeouts.
  * @see the composing traits for javadocs.
  * @param underlying The Salad API to augment.
  * @param timeout The timeout to use for all requests.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam SALAD The Salad API.
  * @tparam LETTUCE The Lettuce API.
  */
class SaladTimeoutAPI[EK,EV,SALAD,LETTUCE]
(val underlying: SALAD
  with SaladHashCommands[EK,EV,LETTUCE]
  with SaladKeyCommands[EK,EV,LETTUCE]
  with SaladStringCommands[EK,EV,LETTUCE],
 val timeout: FiniteDuration = Timeout.default)
  extends SaladHashCommands[EK,EV,LETTUCE]
    with SaladKeyCommands[EK,EV,LETTUCE]
    with SaladStringCommands[EK,EV,LETTUCE] {

  /**
    * Race a request against a timer.
    * @param request The Redis request.
    * @param executionContext The thread dispatcher.
    * @tparam T The future return value.
    * @return The result of the completed request if it finishes before the timer. Otherwise return
    *         a failed future.
    */
  private def timedRequest[T](request: Future[T])
                             (implicit executionContext: ExecutionContext)
  : Future[T] = {
    val timer: Future[T] = Future {
      Thread.sleep(Timeout.default.toMillis)
      throw new Exception("Request timed out")
    }.flatMap(_ => request)
    Future.firstCompletedOf(List(request, timer))
  }

  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.hdel(key, field))

  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)

  : Future[Option[DV]] =
    timedRequest(underlying.hget(key, field))

  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.hset(key, field, value))

  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.del(key))

  def expire[DK](key: DK, ex: Long)
                (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.expire(key, ex))

  def migrate[DK](redisURI: RedisURI, keys: List[DK], timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Unit] =
    timedRequest(underlying.migrate(redisURI, keys, timeout, copy, replace))

  def pexpire[DK](key: DK, px: Long)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.pexpire(key, px))

  def persist[DK](key: DK)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] =
    timedRequest(underlying.persist(key))

  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]] =
    timedRequest(underlying.get(key))

  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Unit] =
    timedRequest(underlying.set(key, value, ex, px, nx, xx))

}
