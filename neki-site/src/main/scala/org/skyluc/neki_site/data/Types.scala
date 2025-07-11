package org.skyluc.neki_site.data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.SimpleError
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
        Left(SimpleError("A processor for neki_site elements is required"))
    }
  }

  def process[A](processor: ProcessorWithError[A]): Either[BaseError, A]
}

trait WithProcessorElement extends fr.WithProcessorElement {

  override def process[T](processor: fr.ProcessorElement[T]): T = {
    processor match {
      case p: Processor[T] =>
        process(p)
      case _ =>
        ???
    }
  }

  def process[T](processor: ProcessorElement[T]): T

}

trait Processor[T] extends fr.Processor[T] with ProcessorElement[T] {

  def processSite(site: Site): T

}

trait ProcessorElement[T] extends fr.ProcessorElement[T] {
  def processChronologyPage(chronologyPage: ChronologyPage): T

  def processMusicPage(musicPage: MusicPage): T

  def processShowsPage(showsPage: ShowsPage): T

}
