package com.kliewkliew.salad.serde

/**
  * A serializer-deserializer interface.
  * @tparam D The decoded type.
  * @tparam E The encoded type.
  */
trait Serde[D,E] {
  def serialize(a: D): E
  def deserialize(b: E): D
}
