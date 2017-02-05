name := "salad"
organization := "com.github.kliewkliew"

version := "0.11.02"

scalaVersion := "2.11.8"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/service/repositories/releases/"

libraryDependencies ++= Seq(
  "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1",
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "org.scala-lang.modules" % "scala-java8-compat_2.11" % "0.8.0",
  "org.xerial.snappy" % "snappy-java" % "1.1.2.1",
  "org.slf4j" % "slf4j-api" % "1.7.22"
)
