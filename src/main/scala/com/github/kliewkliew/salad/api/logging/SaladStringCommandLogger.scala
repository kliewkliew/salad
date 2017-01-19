package com.github.kliewkliew.salad.api.logging

import scala.util.{Failure, Success, Try}

object SaladStringCommandLogger extends BinaryLogger("SaladStringCommands") {

  def get[DK,DV](key: DK)
                (result: Try[Option[DV]]) =
    result match {
      case Success(Some(value)) =>
        success.log(s"Got key, value: $key, $value")
      case Success(None) =>
        success.log(s"No value for key: $key")
      case Failure(t) =>
        failure.log(
          s"Failed to get value for key: $key", t)
    }

  def set[DK,DV](key: DK, value: DV,
                 ex: Option[Long], px: Option[Long],
                 nx: Boolean, xx: Boolean)
                (result: Try[Unit]) =
    result match {
      case Success(_) =>
        success.log(s"Set key, value: $key, $value")
      case Failure(t) =>
        failure.log(
          s"Failed to set key, value: $key, $value", t)
    }

}
