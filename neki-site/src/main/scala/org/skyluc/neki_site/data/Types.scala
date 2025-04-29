package org.skyluc.neki_site.data

import org.skyluc.fan_resources.BaseError
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

  override def process[A](processor: fr.ProcessorWithError[A]): Either[BaseError, A] = {
    processor match {
      case p: ProcessorWithError[A] =>
        process(p)
      case _ =>
        ???
    }
  }

  def process[A](processor: ProcessorWithError[A]): Either[BaseError, A]
}
