package redis.serde

import org.xerial.snappy.Snappy

/**
  * Serialize type as a Snappy-compressed byte array
  */
object SnappySerdes {

  implicit val byteArraySerde = new SnappyByteArraySerde

  implicit val stringSerde = new SnappyStringSerde

  implicit val byteSerde = new SnappyByteSerde

  implicit val shortSerde = new SnappyShortSerde

  implicit val intSerde = new SnappyIntSerde

  implicit val longSerde = new SnappyLongSerde

  implicit val floatSerde = new SnappyFloatSerde

  implicit val doubleSerde = new SnappyDoubleSerde

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

class SnappyByteSerde extends ByteSerde {
  override def serialize(a: Byte): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Byte =
    super.deserialize(b)
}

class SnappyShortSerde extends ShortSerde {
  override def serialize(a: Short): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Short =
    super.deserialize(b)
}

class SnappyIntSerde extends IntSerde {
  override def serialize(a: Int): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Int =
    super.deserialize(b)
}

class SnappyLongSerde extends LongSerde {
  override def serialize(a: Long): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Long =
    super.deserialize(b)
}

class SnappyFloatSerde extends FloatSerde {
  override def serialize(a: Float): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Float =
    super.deserialize(b)
}

class SnappyDoubleSerde extends DoubleSerde {
  override def serialize(a: Double): Array[Byte] =
    super.serialize(a)

  override def deserialize(b: Array[Byte]): Double =
    super.deserialize(b)
}
