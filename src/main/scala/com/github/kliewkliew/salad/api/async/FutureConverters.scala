package com.github.kliewkliew.salad.api.async

import com.lambdaworks.redis.RedisFuture

import scala.compat.java8.FutureConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object FutureConverters {

  /**
    * Implicitly convert Future Java types into Future Scala types.
    * Implicit conversions chain as follows:
    *   RedisFuture[JavaType] -> Future[JavaType] -> Future[ScalaType]
    */
  implicit def CompletionStageToFuture[J](in: RedisFuture[J]): Future[J] =
    in.toScala

  implicit def RedisFutureJavaBooleanToFutureScalaBoolean(in: RedisFuture[java.lang.Boolean])
                                                         (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    in.toScala
  implicit def FutureJavaBooleanToFutureScalaBoolean(in: Future[java.lang.Boolean])
                                                    (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    in.map(_ == true)

  implicit def RedisFutureJavaLongToFutureScalaBoolean(in: RedisFuture[java.lang.Long])
                                                      (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    in.toScala
  implicit def FutureJavaLongToFutureScalaBoolean(in: Future[java.lang.Long])
                                                 (implicit executionContext: ExecutionContext)
  : Future[Boolean] =
    in.map(_ == 1)

  implicit def RedisFutureJavaLongToFutureScalaLong(in: RedisFuture[java.lang.Long])
                                                   (implicit executionContext: ExecutionContext)
  : Future[Long] =
    in.toScala
  implicit def FutureJavaLongToFutureScalaLong(in: Future[java.lang.Long])
                                              (implicit executionContext: ExecutionContext)
  : Future[Long] =
    in.map(_.toLong)

  /**
    * These implicits are apt to cause compiler problems so they are implemented as wrappers that
    * must be invoked manually.
    *   ie. saladAPI.api.clusterReplicate(poorestMaster).isOK
    */

  // Ensure that unchecked exceptions can be mapped over.
  implicit class TryToFuture[J](in: Try[RedisFuture[J]]) {
    def toFuture: Future[J] = in match {
      case Success(future) => future
      case Failure(t) => Future.failed(t)
    }
  }

  // For simple-string-reply, we get either success or an exception
  // which maps to either Future.success or Future.failed
  implicit class FutureStringToFutureUnit(in: Future[String]) {
    def isOK(implicit executionContext: ExecutionContext)
    : Future[Unit] = in.map {
      case "OK" => Future.successful(Unit)
      case err => Future.failed(new Exception(err))
    }
  }

}
