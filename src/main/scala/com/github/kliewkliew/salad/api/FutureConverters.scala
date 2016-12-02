package com.github.kliewkliew.salad.api

import com.lambdaworks.redis.RedisFuture

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

object FutureConverters {

  /**
    * Implicitly convert Future Java types into Future Scala types.
    * Implicit conversions chain as follows:
    *   RedisFuture[JavaType] -> Future[JavaType] -> Future[ScalaType]
    */
  implicit def CompletionStageToFuture[J](in: RedisFuture[J]): Future[J] =
    in.toScala

  implicit def RedisFutureJavaBooleanToFutureScalaBoolean(in: RedisFuture[java.lang.Boolean]): Future[Boolean] =
    in.toScala
  implicit def FutureJavaBooleanToFutureScalaBoolean(in: Future[java.lang.Boolean]): Future[Boolean] =
    in.map(_ == true)

  implicit def RedisFutureJavaLongToFutureScalaBoolean(in: RedisFuture[java.lang.Long]): Future[Boolean] =
    in.toScala
  implicit def FutureJavaLongToFutureScalaBoolean(in: Future[java.lang.Long]): Future[Boolean] =
    in.map(_ == 1)

  /**
    * These implicits are apt to cause compiler problems so they are wrappers that must be invoked manually.
    *   ie. saladAPI.api.clusterReplicate(poorestMaster).isOK
    */
  implicit class RedisFutureStringToFutureScalaBoolean(in: RedisFuture[String]) {
    def isOK: Future[Boolean] = in.map(_ == "OK")
  }

}
