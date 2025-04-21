package org.skyluc.collection

type LayeredData[T] = List[LayeredNode[T]]

case class LayeredNode[T](
    data: T,
    subLayer: LayeredData[T],
)

object LayeredNode {
  def apply[T](data: T): LayeredNode[T] = LayeredNode(data, Nil)
}
