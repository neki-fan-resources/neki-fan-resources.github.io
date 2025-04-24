package org.skyluc.fan_resources.data

trait Data {
  val all: Map[Id[?], Datum[?]]

  def get[T <: Datum[T]](id: Id[T]): T = {
    all(id).asInstanceOf[T]
  }
}
