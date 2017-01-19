package com.github.kliewkliew.salad.api.sync

import com.lambdaworks.redis.api.sync._

/**
  * Wrap the lettuce API to provide an idiomatic Scala API.
  * @param underlying The lettuce sync API to be wrapped.
  * @tparam EK The key storage encoding.
  * @tparam EV The value storage encoding.
  * @tparam API The lettuce API to wrap.
  */
case class SyncSaladAPISync[EK,EV,API]
(underlying: API
  with RedisHashCommands[EK,EV]
  with RedisKeyCommands[EK,EV]
  with RedisStringCommands[EK,EV]
)
  extends SaladHashSyncCommands[EK,EV,API]
    with SaladKeySyncCommands[EK,EV,API]
    with SaladStringSyncCommands[EK,EV,API]
