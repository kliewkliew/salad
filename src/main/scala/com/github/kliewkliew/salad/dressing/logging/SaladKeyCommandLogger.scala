package com.github.kliewkliew.salad.dressing.logging

import com.lambdaworks.redis.RedisURI

import scala.util.{Failure, Success, Try}

object SaladKeyCommandLogger extends BinaryLogger("SaladKeyCommands") {

  def del[DK](key: DK)
             (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Deleted key: $key")
      case Failure(t) =>
        failure.log(
          s"Failed to delete key: $key", t)
    }

  def expire[DK](key: DK, ex: Long)
                (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Expire key after $ex seconds: $key")
      case Failure(t) =>
        failure.log(s"Failed to set key expiry to $ex seconds: $key", t)
    }

  def migrate[DK](redisURI: RedisURI, keys: List[DK], timeout: Long,
                  copy: Boolean = false, replace: Boolean)
                 (result: Try[Unit]) =
    result match {
      case Success(_) =>
        hardcoded.trace(s"Migrating to $redisURI keys: $keys")
      case Failure(t) =>
        hardcoded.trace(s"Failed to migrate to $redisURI keys: $keys", t)
    }

  def pexpire[DK](key: DK, px: Long)
                 (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Expire key after $px millseconds: $key")
      case Failure(t) =>
        failure.log(s"Failed to set key expiry to $px milliseconds: $key", t)
    }

  def persist[DK](key: DK)
                 (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Removed expiry from key: $key")
      case Failure(t) =>
        failure.log(
          s"Failed to remove expiry from key: $key", t)
    }

}
