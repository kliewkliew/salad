# Salad
Salad wraps the lettuce async and sync Java API to provide an idiomatic API for Scala applications.

Efficient serdes (serializer-deserializers) are provided to encode keys and values as plain byte-arrays or Snappy-compressed byte-arrays.
CompactByteArraySerdes and SnappySerdes will also compact numeric values to the smallest possible lossless representation.

Single-node Redis, master-slave Sentinel configurations, and sharded Redis Cluster configurations are supported.
Notably, this is the first Scala client to support Redis Cluster *and* provide an asynchronous API together in one package.

Salad also ensures that all exceptions thrown by lettuce can be mapped over in Scala futures.
If you used lettuce directly, exceptions in lettuce might not trigger `Future.failed` in Scala.

# Usage
`SaladAPI` is the base wrapper around the lettuce API.
It can be decorated with various wrappers that add additional functionality.

## Instantiate Lettuce API
```
val client = RedisClient.create("redis://localhost")
val lettuceAPI = client.connect(ByteArrayCodec.INSTANCE).async
```

## Instantiate Salad Wrapper
If the key can be a string, byte-array, or a numeric type:
```
import redis.serde.SnappySerdes._
val saladAPI =
  new SaladAPI(lettuceAPI)
```
If the unencoded key will always be a (compressable) string and the value can be any type:
```
import redis.serde.SnappySerdes._
val saladAPI =
  new SaladStringKeyAPI(
    new SaladAPI(lettuceAPI))
```
If the unencoded key will always be an uncompressable string and the value can be any type:
```
import redis.serde.SnappySerdes._
val saladAPI =
  new SaladUIIDKeyAPI(
    new SaladAPI(lettuceAPI))
```

If the strings and byte-array values are not compressible while using SaladStringKeyAPI or SaladUIIDKeyAPI, import `CompactByteArraySerdes` to only compact numeric types.

## Use Salad
To use Snappy compression for strings and byte-arays (and compaction for numeric types):
```
val got: Future[Option[Int]] =
  saladAPI.get[Int]("test")
    .map(valueOpt => valueOpt.map(_ + 1))
```
However, if the key string is a uuid, there may not be a lot of repetition in the key and thus compression will only add overhead.
In that case, you can pass the key and value serdes explicitly.

```
val got: Future[Option[Int]] =
  saladAPI.get[Int]("test")
      (redis.serde.ByteArraySerdes.stringSerde,
       redis.serde.CompactByteArraySerdes.intSerde)
    .map(valueOpt => valueOpt.map(_ + 1))
```

If the key is always a string UIID and the value is always numeric or a compressable string or byte-array, you can use the simplified `SaladUIIDKeyAPI`.

You must ensure that a serde pair is used symmetrically for mutating and accessing a key-value pair.

## Serde-Codec Choices
The lettuce codec determines the data is encoded over the wire.
Each connection may have one codec.
You can create multiple connections with different codecs, sharing underlying client resources.
But with interchangeable serdes, that is no longer necessary.

The serde determines how various Scala types are serialized to the codec format and provides type safety to the API.
Each interaction with Redis can use a different serde.

The recommended pair is ByteArrayCodec with SnappySerdes or CompactByteArraySerdes (for strings that will not compress well).

CompressionCodec is not recommended as it will attempt to compress singular numeric types which just adds CPU and byte-size overhead.

String serdes are also provided if you require readable keys/values.

### ByteArrayCodec
* ByteArraySerdes
* CompactByteArraySerdes
* SnappySerdes

### CompressionCodec
* ByteArraySerdes
* CompactByteArraySerdes

### StringCodec, Utf8StringCodec
* StringSerdes

## Decorators
### Configuration
Decorators are configurable by setting a configuration file for the app.
```
-Dconfig.file=src/main/resources/application.conf
```
### Exception Handling
To ensure that exceptions are thrown when all Redis nodes go down, use the `SaladTimeoutAPI` decorator.
```
import scala.concurrent.duration._
val saladAPI =
  new SaladUIIDKeyAPI(
    new SaladTimeoutAPI(
      new SaladAPI(lettuceAPI),
      500.milliseconds))
```
If you omit the timeout duration parameter, you can specify the timeout (milliseconds) in your configuration file.

ie.
```
salad.request-timeout = 200
```

### Logging
To log commands to `sl4j`, use the `SaladLoggingAPI` decorator.
```
import scala.concurrent.duration._
val saladAPI =
  new SaladUIIDKeyAPI(
    new SaladLoggingAPI(
      new SaladTimeoutAPI(
        new SaladAPI(lettuceAPI),
        500.milliseconds)))
```
By default: success and failure are logged to `DEBUG` and `WARN`.
The default log levels can be overriden in the configuration file.

ie.
```
salad {
  logger {
    success = "INFO"
    failure = "ERROR"
  }
}
```

# SBT
TODO: publish jars to Maven repo

Salad depends on lettuce 5.x.

```
libraryDependencies += "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1"
```

## Netty Version Conflict
If the Netty version of lettuce conflicts with your application (ie. Play 2.5), add an exclusion rule to lettuce.
```
libraryDependencies += "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1" excludeAll ExclusionRule(organization = "io.netty")
```
If this doesn't work, you may need a lettuce jar with shaded dependencies.