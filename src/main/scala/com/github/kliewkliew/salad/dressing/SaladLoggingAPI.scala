package com.github.kliewkliew.salad.dressing

import com.github.kliewkliew.salad.api.{SaladHashCommands, SaladKeyCommands, SaladStringCommands}
import com.github.kliewkliew.salad.dressing.logging.{SaladHashCommandLogger, SaladKeyCommandLogger, SaladStringCommandLogger}
import com.github.kliewkliew.salad.serde.Serde
import com.lambdaworks.redis.RedisURI

import scala.concurrent.{ExecutionContext, Future}

/**
  * Decorate a Salad API with sl4j logging.
  * This wrapper will only log errors from the wrapped API (not from a wrapping API).
  * @see the composing traits for javadocs.
  * @param underlying The Salad API to augment.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam SALAD The Salad API.
  * @tparam LETTUCE The Lettuce API.
  */
class SaladLoggingAPI[EK,EV,SALAD,LETTUCE]
(val underlying: SALAD
  with SaladHashCommands[EK,EV,LETTUCE]
  with SaladKeyCommands[EK,EV,LETTUCE]
  with SaladStringCommands[EK,EV,LETTUCE])
  extends SaladHashCommands[EK,EV,LETTUCE]
    with SaladKeyCommands[EK,EV,LETTUCE]
    with SaladStringCommands[EK,EV,LETTUCE] {

  def hdel[DK](key: DK, field: DK)
              (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.hdel(key, field)
    resultF.onComplete(result => SaladHashCommandLogger.hdel(key, field)(result))
    resultF
  }

  def hget[DK,DV](key: DK, field: DK)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)

  : Future[Option[DV]] = {
    val resultF = underlying.hget(key, field)
    resultF.onComplete(result => SaladHashCommandLogger.hget(key, field)(result))
    resultF
  }

  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean = false)
                 (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.hset(key, field, value)
    resultF.onComplete(result => SaladHashCommandLogger.hset(key, field, value, nx)(result))
    resultF
  }

  def del[DK](key: DK)
             (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.del(key)
    resultF.onComplete(result => SaladKeyCommandLogger.del(key)(result))
    resultF
  }

  def expire[DK](key: DK, ex: Long)
                (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.expire(key, ex)
    resultF.onComplete(result => SaladKeyCommandLogger.expire(key, ex)(result))
    resultF
  }

  def migrate[DK](redisURI: RedisURI, keys: List[DK], timeout: Long = 5000,
                  copy: Boolean = false, replace: Boolean = false)
                 (implicit keySerde: Serde[DK,EK],
                  executionContext: ExecutionContext)
  : Future[Unit] = {
    val resultF = underlying.migrate(redisURI, keys, timeout, copy, replace)
    resultF.onComplete(result => SaladKeyCommandLogger.migrate(redisURI, keys, timeout, copy, replace)(result))
    resultF
  }

  def pexpire[DK](key: DK, px: Long)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.pexpire(key, px)
    resultF.onComplete(result => SaladKeyCommandLogger.pexpire(key, px)(result))
    resultF
  }

  def persist[DK](key: DK)
                 (implicit keySerde: Serde[DK,EK], executionContext: ExecutionContext)
  : Future[Boolean] = {
    val resultF = underlying.persist(key)
    resultF.onComplete(result => SaladKeyCommandLogger.persist(key)(result))
    resultF
  }

  def get[DK,DV](key: DK)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Option[DV]] = {
    val resultF = underlying.get(key)
    resultF.onComplete(result => SaladStringCommandLogger.get(key)(result))
    resultF
  }

  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long] = None, px: Option[Long] = None,
                 nx: Boolean = false, xx: Boolean = false)
                (implicit keySerde: Serde[DK,EK], valSerde: Serde[DV,EV], executionContext: ExecutionContext)
  : Future[Unit] = {
    val resultF = underlying.set(key, value, ex, px, nx, xx)
    resultF.onComplete(result => SaladStringCommandLogger.set(key, value, ex, px, nx, xx)(result))
    resultF
  }

}
