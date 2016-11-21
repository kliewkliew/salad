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
```
import redis.serde.SnappySerdes._
val got: Future[Option[Int]] =
  saladAPI.get[Int]("test").map(valueOpt => valueOpt.map(_ + 1))
```

# SBT
TODO: Maven repo

## Netty Version Conflict
If the Netty version conflicts with your application (ie. Play 2.5), add the following inline to `libraryDependencies +=`:
```
excludeAll ExclusionRule(organization = "io.netty")
```
If this doesn't work, you may need a jar with shaded dependencies.