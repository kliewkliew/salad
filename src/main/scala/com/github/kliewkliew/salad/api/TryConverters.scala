package com.github.kliewkliew.salad.api

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object TryConverters {

  implicit def TryJavaBooleanToTryScalaBoolean(in: Try[java.lang.Boolean])
  : Try[Boolean] =
    in.map(_ == true)

  implicit def TryJavaLongToTryScalaLong(in: Try[java.lang.Long])
  : Try[Long] =
    in.map(_.toLong)

  implicit def TryJavaLongToTryScalaBoolean(in: Try[java.lang.Long])
  : Try[Boolean] =
    in.map(_ == 1)

  implicit def TrySimpleStringReply(in: Try[String])
  : Try[Unit] =
    in match {
      case Success("OK") => Success(Unit)
      case Success(err) => Failure(new Exception(err))
      case Failure(t) => Failure(t)
    }

}
