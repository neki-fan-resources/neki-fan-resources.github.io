package org.skyluc.neki_site.data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data as fr

object PageId {
  private val GEN = "page"
  def apply(id: String, dark: Boolean = false): fr.GenId[Page] = fr.GenId[Page](GEN, id, dark)
}

sealed trait Page extends fr.Datum[Page] {
  val id: fr.GenId[Page]
}

case class MusicPage(
    id: fr.GenId[Page],
    music: List[fr.AlbumId | fr.SongId],
    linkedTo: Seq[fr.Id[?]],
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  override def errored(): MusicPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): MusicPage = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMusicPage(this)

  override def process[A](processor: ProcessorWithError[A]): Either[BaseError, A] = {
    processor.processMusicPage(this)
  }

}

case class ShowsPage(
    id: fr.GenId[Page],
    shows: List[fr.ShowId | fr.TourId],
    linkedTo: Seq[fr.Id[?]],
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  override def errored(): ShowsPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): ShowsPage = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processShowsPage(this)

  override def process[A](processor: ProcessorWithError[A]): Either[BaseError, A] = {
    processor.processShowsPage(this)
  }

}

case class ChronologyPage(
    id: fr.GenId[Page],
    chronology: fr.Chronology,
    linkedTo: Seq[fr.Id[?]] = Nil,
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  override def errored(): ChronologyPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): ChronologyPage = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processChronologyPage(this)

  override def process[A](processor: ProcessorWithError[A]): Either[BaseError, A] = {
    processor.processChronologyPage(this)
  }

}

object ChronologyPage {
  val MARKER_DESIGNATION = "marker"
}

object Pages {
  val ID_BASE_PATH = "page"
}
