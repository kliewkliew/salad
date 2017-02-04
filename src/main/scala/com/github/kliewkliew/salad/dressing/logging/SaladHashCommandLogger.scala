package com.github.kliewkliew.salad.dressing.logging

import scala.util.{Failure, Success, Try}

object SaladHashCommandLogger extends BinaryLogger("SaladHashCommands") {

  def hdel[DK](key: DK, field: DK)
              (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Deleted key, field: $key, $field")
      case Failure(t) =>
        failure.log(
          s"Failed to delete key, field: $key, $field", t)
    }

  def hget[DK,DV](key: DK, field: DK)
                 (result: Try[Option[DV]]) =
    result match {
      case Success(Some(value)) =>
        success.log(s"Got key, field, value: $key, $field, $value")
      case Success(None) =>
        success.log(s"No value for key, field: $key, $field")
      case Failure(t) =>
        failure.log(
          s"Failed to get value for key, field: $key, $field", t)
    }

  def hset[DK,DV](key: DK, field: DK, value: DV,
                  nx: Boolean)
                 (result: Try[Boolean]) =
    result match {
      case Success(_) =>
        success.log(s"Set key, field, value, nx: $key, $field, $value, $nx")
      case Failure(t) =>
        failure.log(
          s"Failed to set key, field, value, nx: $key, $field, $value, $nx", t)
    }

}
