package com.kliewkliew.salad.serde

object StringSerdes {

  implicit val byteArraySerde = new ByteArrayStringSerde

  implicit val stringSerde = new StringStringSerde

  implicit val byteSerde = new ByteStringSerde

  implicit val shortSerde = new ShortStringSerde

  implicit val intSerde = new IntStringSerde

  implicit val longSerde = new LongStringSerde

  implicit val floatSerde = new FloatStringSerde

  implicit val doubleSerde = new DoubleStringSerde

}


class ByteArrayStringSerde extends Serde[Array[Byte], String] {
  override def serialize(a: Array[Byte]): String =
    new String(a)

  override def deserialize(b: String): Array[Byte] =
    b.getBytes
}

class StringStringSerde extends Serde[String, String] {
  override def serialize(a: String): String =
    a

  override def deserialize(b: String): String =
    b
}

class ByteStringSerde extends Serde[Byte,String] {
  override def serialize(a: Byte): String =
    new String(Array(a))

  override def deserialize(b: String): Byte =
    b.getBytes()(0)
}

class ShortStringSerde extends Serde[Short,String] {
  override def serialize(a: Short): String =
    a.toString

  override def deserialize(b: String): Short =
    b.toShort
}

class IntStringSerde extends Serde[Int,String] {
  override def serialize(a: Int): String =
    a.toString

  override def deserialize(b: String): Int =
    b.toInt
}

class LongStringSerde extends Serde[Long,String] {
  override def serialize(a: Long): String =
    a.toString

  override def deserialize(b: String): Long =
    b.toLong
}

class FloatStringSerde extends Serde[Float,String] {
  override def serialize(a: Float): String =
    a.toString

  override def deserialize(b: String): Float =
    b.toFloat
}

class DoubleStringSerde extends Serde[Double,String] {
  override def serialize(a: Double): String =
    a.toString

  override def deserialize(b: String): Double =
    b.toDouble
}
