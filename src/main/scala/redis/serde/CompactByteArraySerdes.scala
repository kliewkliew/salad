package redis.serde

import java.nio.ByteBuffer

/**
  * Serialize array-types as plain byte arrays.
  * Serialize numeric types as compacted byte arrays.
  */
object CompactByteArraySerdes {

  implicit val byteArraySerde = new ByteArraySerde

  implicit val stringSerde = new StringSerde

  implicit val byteSerde = new ByteSerde

  implicit val shortSerde = new CompactShortSerde

  implicit val intSerde = new CompactIntSerde

  implicit val longSerde = new CompactLongSerde

  implicit val floatSerde = new CompactFloatSerde

  implicit val doubleSerde = new CompactDoubleSerde

}

class CompactShortSerde extends Serde[Short,Array[Byte]] {
  override def serialize(a: Short): Array[Byte] =
    ByteBuffer.allocate(java.lang.Short.BYTES).putShort(a)
      .array().dropWhile(_ == 0)

  override def deserialize(b: Array[Byte]): Short =
    ByteBuffer.wrap(new Array[Byte](java.lang.Short.BYTES - b.length) ++ b)
      .getShort
}

class CompactIntSerde extends Serde[Int,Array[Byte]] {
  override def serialize(a: Int): Array[Byte] =
    ByteBuffer.allocate(java.lang.Integer.BYTES).putInt(a)
      .array().dropWhile(_ == 0)

  override def deserialize(b: Array[Byte]): Int =
    ByteBuffer.wrap(new Array[Byte](java.lang.Integer.BYTES - b.length) ++ b)
      .getInt
}

class CompactLongSerde extends Serde[Long,Array[Byte]] {
  override def serialize(a: Long): Array[Byte] =
    ByteBuffer.allocate(java.lang.Long.BYTES).putLong(a)
      .array().dropWhile(_ == 0)

  override def deserialize(b: Array[Byte]): Long =
    ByteBuffer.wrap(new Array[Byte](java.lang.Long.BYTES - b.length) ++ b)
      .getLong
}

class CompactFloatSerde extends Serde[Float,Array[Byte]] {
  override def serialize(a: Float): Array[Byte] =
    ByteBuffer.allocate(java.lang.Float.BYTES).putFloat(a)
      .array().dropWhile(_ == 0)

  override def deserialize(b: Array[Byte]): Float =
    ByteBuffer.wrap(new Array[Byte](java.lang.Float.BYTES - b.length) ++ b)
      .getFloat
}

class CompactDoubleSerde extends Serde[Double,Array[Byte]] {
  override def serialize(a: Double): Array[Byte] =
    ByteBuffer.allocate(java.lang.Double.BYTES).putDouble(a)
      .array().dropWhile(_ == 0)

  override def deserialize(b: Array[Byte]): Double =
    ByteBuffer.wrap(new Array[Byte](java.lang.Double.BYTES - b.length) ++ b)
      .getDouble
}
