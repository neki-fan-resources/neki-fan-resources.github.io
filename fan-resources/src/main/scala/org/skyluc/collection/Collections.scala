package org.skyluc.collection

object Collections {

  def applyToHead[A, B >: A](l: List[A])(f: (A) => B): List[B] = {
    l.take(1).map(f) ::: l.drop(1)
  }

  def applyToTail[A, B >: A](l: List[A])(f: (A) => B): List[B] = {
    l.take(1) ::: l.drop(1).map(f)
  }
}
