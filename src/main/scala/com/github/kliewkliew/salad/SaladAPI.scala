package com.github.kliewkliew.salad

import com.github.kliewkliew.salad.api.{SaladClusterCommands, SaladHashCommandsImpl, SaladKeyCommandsImpl, SaladStringCommandsImpl}
import com.lambdaworks.redis.api.async._
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @see the composing traits for javadocs.
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
class SaladAPI[EK,EV,API]
(val underlying: API
  with RedisHashAsyncCommands[EK,EV]
  with RedisKeyAsyncCommands[EK,EV]
  with RedisStringAsyncCommands[EK,EV]
)
  extends SaladHashCommandsImpl[EK,EV,API]
    with SaladKeyCommandsImpl[EK,EV,API]
    with SaladStringCommandsImpl[EK,EV,API]

/**
  * Wrap the lettuce cluster-administration API to provide an idiomatic Scala API.
  *
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  */
class SaladClusterAPI[EK,EV](val underlying: RedisClusterAsyncCommands[EK,EV])
  extends SaladClusterCommands[EK,EV,RedisClusterAsyncCommands[EK,EV]]
    with SaladKeyCommandsImpl[EK,EV,RedisKeyAsyncCommands[EK,EV]]