package redis.serde

import org.xerial.snappy.Snappy

/**
  * Serialize array-types as Snappy-compressed byte arrays.
  * Serialize numeric types as compacted byte arrays.
  */
object SnappySerdes {

  implicit val byteArraySerde = new SnappyByteArraySerde

  implicit val stringSerde = new SnappyStringSerde

  implicit val byteSerde = new ByteSerde

  implicit val shortSerde = new CompactShortSerde

  implicit val intSerde = new CompactIntSerde

  implicit val longSerde = new CompactLongSerde

  implicit val floatSerde = new CompactFloatSerde

  implicit val doubleSerde = new CompactDoubleSerde

}

class SnappyByteArraySerde extends ByteArraySerde {
  override def serialize(a: Array[Byte]): Array[Byte] =
    Snappy.compress(super.serialize(a))

  override def deserialize(b: Array[Byte]): Array[Byte] =
    super.deserialize(Snappy.uncompress(b))
}

class SnappyStringSerde extends StringSerde {
  override def serialize(a: String): Array[Byte] =
    Snappy.compress(super.serialize(a))

  override def deserialize(b: Array[Byte]): String =
    super.deserialize(Snappy.uncompress(b))
}
