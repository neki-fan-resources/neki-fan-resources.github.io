package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

trait WithProcessor extends fr.WithProcessor {

  override def process[T](processor: fr.Processor[T]): T = {
    processor match {
      case p: Processor[T] =>
        process(p)
      case _ =>
        ???
    }
  }

  def process[T](processor: Processor[T]): T

  override def process[E, A](processor: fr.ProcessorWithError[E, A]): Either[E, A] = {
    processor match {
      case p: ProcessorWithError[E, A] =>
        process(p)
      case _ =>
        ???
    }
  }

  def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A]
}
