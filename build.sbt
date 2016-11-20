name := "salad"

version := "0.9"

scalaVersion := "2.11.8"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/service/repositories/releases/"

libraryDependencies ++= Seq(
  "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1",
  "com.gilt" %% "gfc-guava" % "0.2.3",
  "org.xerial.snappy" % "snappy-java" % "1.1.2.1"
)

