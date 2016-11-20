package redis.serde

import java.nio.ByteBuffer

/**
  * Serialize type as a plain byte array
  */
object ByteArraySerdes {

  implicit val byteArraySerde = new ByteArraySerde

  implicit val stringSerde = new StringSerde

  implicit val byteSerde = new ByteSerde

  implicit val shortSerde = new ShortSerde

  implicit val intSerde = new IntSerde

  implicit val longSerde = new LongSerde

  implicit val floatSerde = new FloatSerde

  implicit val doubleSerde = new DoubleSerde

}

class ByteArraySerde extends Serde[Array[Byte], Array[Byte]] {
  override def serialize(a: Array[Byte]): Array[Byte] =
    a

  override def deserialize(b: Array[Byte]): Array[Byte] =
    b
}

class StringSerde extends Serde[String, Array[Byte]] {
  override def serialize(a: String): Array[Byte] =
    a.getBytes()

  override def deserialize(b: Array[Byte]): String =
    new String(b)
}

class ByteSerde extends Serde[Byte,Array[Byte]] {
  override def serialize(a: Byte): Array[Byte] =
    Array(a)

  override def deserialize(b: Array[Byte]): Byte =
    b(0)
}

class ShortSerde extends Serde[Short,Array[Byte]] {
  override def serialize(a: Short): Array[Byte] =
    ByteBuffer.allocate(java.lang.Short.BYTES).putShort(a).array()

  override def deserialize(b: Array[Byte]): Short =
    ByteBuffer.wrap(b).getShort
}

class IntSerde extends Serde[Int,Array[Byte]] {
  override def serialize(a: Int): Array[Byte] =
    ByteBuffer.allocate(java.lang.Integer.BYTES).putInt(a).array()

  override def deserialize(b: Array[Byte]): Int =
    ByteBuffer.wrap(b).getInt
}

class LongSerde extends Serde[Long,Array[Byte]] {
  override def serialize(a: Long): Array[Byte] =
    ByteBuffer.allocate(java.lang.Long.BYTES).putLong(a).array()

  override def deserialize(b: Array[Byte]): Long =
    ByteBuffer.wrap(b).getLong
}

class FloatSerde extends Serde[Float,Array[Byte]] {
  override def serialize(a: Float): Array[Byte] =
    ByteBuffer.allocate(java.lang.Float.BYTES).putFloat(a).array()

  override def deserialize(b: Array[Byte]): Float =
    ByteBuffer.wrap(b).getFloat
}

class DoubleSerde extends Serde[Double,Array[Byte]] {
  override def serialize(a: Double): Array[Byte] =
    ByteBuffer.allocate(java.lang.Double.BYTES).putDouble(a).array()

  override def deserialize(b: Array[Byte]): Double =
    ByteBuffer.wrap(b).getDouble
}
