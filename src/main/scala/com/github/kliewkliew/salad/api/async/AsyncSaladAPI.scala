package com.github.kliewkliew.salad.api.async

import com.lambdaworks.redis.api.async._

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @param underlying The lettuce async API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
case class AsyncSaladAPI[EK,EV,API]
(underlying: API
  with RedisHashAsyncCommands[EK,EV]
  with RedisKeyAsyncCommands[EK,EV]
  with RedisStringAsyncCommands[EK,EV]
)
  extends SaladHashCommands[EK,EV,API]
    with SaladKeyCommands[EK,EV,API]
    with SaladStringCommands[EK,EV,API]
{
  // Nothing
}
