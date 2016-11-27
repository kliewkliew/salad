package com.kliewkliew.salad.serde

import org.xerial.snappy.Snappy

/**
  * Serialize array-types as Snappy-compressed byte arrays.
  * Serialize numeric types as compacted byte arrays.
  */
object SnappySerdes {

  implicit val byteArraySerde = new ByteArraySnappySerde

  implicit val stringSerde = new StringSnappySerde

  implicit val byteSerde = new ByteByteArraySerde

  implicit val shortSerde = new ShortCompactByteArraySerde

  implicit val intSerde = new IntCompactByteArraySerde

  implicit val longSerde = new LongCompactByteArraySerde

  implicit val floatSerde = new FloatCompactByteArraySerde

  implicit val doubleSerde = new DoubleCompactByteArraySerde

}

class ByteArraySnappySerde extends ByteArrayByteArraySerde {
  override def serialize(a: Array[Byte]): Array[Byte] =
    Snappy.compress(super.serialize(a))

  override def deserialize(b: Array[Byte]): Array[Byte] =
    super.deserialize(Snappy.uncompress(b))
}

class StringSnappySerde extends StringByteArraySerde {
  override def serialize(a: String): Array[Byte] =
    Snappy.compress(super.serialize(a))

  override def deserialize(b: Array[Byte]): String =
    super.deserialize(Snappy.uncompress(b))
}
