package org.skyluc.scala

object EitherOps {
  def errorsAndValues[E, V](l: Seq[Either[E, V]]): (Seq[E], Seq[V]) = {
    val es = l.flatMap(_.left.toOption)
    val vs = l.flatMap(_.toOption)
    (es, vs)
  }

  def errorsAndValuesFlatten[E, V](l: Seq[Either[E, Seq[V]]]): (Seq[E], Seq[V]) = {
    val es = l.flatMap(_.left.toOption)
    val vs = l.flatMap(_.toOption).flatten
    (es, vs)
  }
}
