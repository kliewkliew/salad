package com.github.kliewkliew.salad.dressing.logging

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

class BinaryLogger(namespace: String) {
  private val underlying = LoggerFactory.getLogger(namespace)
  protected val success = new SuccessLogger(underlying)
  protected val failure = new FailureLogger(underlying)
  protected val hardcoded: Logger = underlying
}

object LoggerConfig {
  private val config = Try(ConfigFactory.load().getConfig("salad.logging"))
  val failureLogLevel: String = config.map(_.getString("failure"))
    .getOrElse("WARN")
  val successLogLevel: String = config.map(_.getString("success"))
    .getOrElse("DEBUG")
}

class SuccessLogger(logger: Logger) {
  val log: String => Unit =
    LoggerConfig.successLogLevel.toUpperCase match {
      case "TRACE" => logger.trace
      case "DEBUG" => logger.debug
      case "INFO" => logger.info
      case "WARN" => logger.warn
      case "ERROR" => logger.error
    }
}

class FailureLogger(logger: Logger) {
  val log: (String, Throwable) => Unit =
    LoggerConfig.failureLogLevel.toUpperCase match {
      case "TRACE" => logger.trace
      case "DEBUG" => logger.debug
      case "INFO" => logger.info
      case "WARN" => logger.warn
      case "ERROR" => logger.error
    }
}
