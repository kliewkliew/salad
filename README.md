# Salad
Salad wraps the lettuce async Java API to provide an idiomatic API for Scala applications.

Efficient serdes (serializer-deserializers) are provided to encode keys and values as plain byte-arrays or Snappy-compressed byte-arrays.

Single-node Redis, master-slave Sentinel configurations, and sharded Redis Cluster configurations are supported.
Notably, this is the first Scala client to support Redis Cluster *and* provide an asynchronous API in one package.

# Usage
## Instantiate Lettuce API
```
val client = RedisClient.create("redis://localhost")
val lettuceAPI = client.connect(ByteArrayCodec.INSTANCE).async
```

## Instantiate Salad Wrapper
 ```
 val saladAPI = SaladStringKeyAPI(lettuceAPI)
 ```

## Use Salad
To use Snappy compression for strings and byte-arays (and compaction for numeric types):
```
import redis.serde.SnappySerdes._
val got: Future[Option[Int]] =
  saladAPI.get[Int]("test").map(valueOpt => valueOpt.map(_ + 1))
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

You need to ensure that a serde is used symmetrically for a key-value pair.

# SBT
TODO: publish jars to Maven repo

## Netty Version Conflict
If the Netty version conflicts with your application (ie. Play 2.5), add the following inline to `libraryDependencies +=`:
```
excludeAll ExclusionRule(organization = "io.netty")
```
If this doesn't work, you may need a jar with shaded dependencies.