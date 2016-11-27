package com.kliewkliew.salad.serde

import java.nio.ByteBuffer

/**
  * Serialize all types as plain byte arrays.
  */
object ByteArraySerdes {

  implicit val byteArraySerde = new ByteArrayByteArraySerde

  implicit val stringSerde = new StringByteArraySerde

  implicit val byteSerde = new ByteByteArraySerde

  implicit val shortSerde = new ShortByteArraySerde

  implicit val intSerde = new IntByteArraySerde

  implicit val longSerde = new LongByteArraySerde

  implicit val floatSerde = new FloatByteArraySerde

  implicit val doubleSerde = new DoubleByteArraySerde

}

class ByteArrayByteArraySerde extends Serde[Array[Byte], Array[Byte]] {
  override def serialize(a: Array[Byte]): Array[Byte] =
    a

  override def deserialize(b: Array[Byte]): Array[Byte] =
    b
}

class StringByteArraySerde extends Serde[String, Array[Byte]] {
  override def serialize(a: String): Array[Byte] =
    a.getBytes()

  override def deserialize(b: Array[Byte]): String =
    new String(b)
}

class ByteByteArraySerde extends Serde[Byte,Array[Byte]] {
  override def serialize(a: Byte): Array[Byte] =
    Array(a)

  override def deserialize(b: Array[Byte]): Byte =
    b(0)
}

class ShortByteArraySerde extends Serde[Short,Array[Byte]] {
  override def serialize(a: Short): Array[Byte] =
    ByteBuffer.allocate(java.lang.Short.BYTES).putShort(a).array()

  override def deserialize(b: Array[Byte]): Short =
    ByteBuffer.wrap(b).getShort
}

class IntByteArraySerde extends Serde[Int,Array[Byte]] {
  override def serialize(a: Int): Array[Byte] =
    ByteBuffer.allocate(java.lang.Integer.BYTES).putInt(a).array()

  override def deserialize(b: Array[Byte]): Int =
    ByteBuffer.wrap(b).getInt
}

class LongByteArraySerde extends Serde[Long,Array[Byte]] {
  override def serialize(a: Long): Array[Byte] =
    ByteBuffer.allocate(java.lang.Long.BYTES).putLong(a).array()

  override def deserialize(b: Array[Byte]): Long =
    ByteBuffer.wrap(b).getLong
}

class FloatByteArraySerde extends Serde[Float,Array[Byte]] {
  override def serialize(a: Float): Array[Byte] =
    ByteBuffer.allocate(java.lang.Float.BYTES).putFloat(a).array()

  override def deserialize(b: Array[Byte]): Float =
    ByteBuffer.wrap(b).getFloat
}

class DoubleByteArraySerde extends Serde[Double,Array[Byte]] {
  override def serialize(a: Double): Array[Byte] =
    ByteBuffer.allocate(java.lang.Double.BYTES).putDouble(a).array()

  override def deserialize(b: Array[Byte]): Double =
    ByteBuffer.wrap(b).getDouble
}
