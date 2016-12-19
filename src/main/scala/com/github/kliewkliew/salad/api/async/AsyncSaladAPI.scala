package com.github.kliewkliew.salad.api.async

import com.lambdaworks.redis.api.async._
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
case class AsyncSaladAPI[EK,EV,API]
(underlying: API
  with RedisClusterAsyncCommands[EK,EV]
  with RedisHashAsyncCommands[EK,EV]
  with RedisKeyAsyncCommands[EK,EV]
  with RedisStringAsyncCommands[EK,EV]
)
  extends SaladClusterCommands[EK,EV,API]
    with SaladHashCommands[EK,EV,API]
    with SaladKeyCommands[EK,EV,API]
    with SaladStringCommands[EK,EV,API]

/**
  * Wrap the lettuce cluster API.
  * Useful when after calling getConnection to connect to a single node.
  *
  * @example def getConnection(redisURI: RedisURI): Try[SaladClusterAPI[String,String]] =
  *          Try(saladAPI.underlying.getConnection(redisURI.getHost, redisURI.getPort)).map(SaladClusterAPI(_))
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
case class SaladClusterAPI[EK,EV](underlying: RedisClusterAsyncCommands[EK,EV])
  extends SaladClusterCommands[EK,EV,RedisClusterAsyncCommands[EK,EV]]