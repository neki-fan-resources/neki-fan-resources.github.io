package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

case class PageId(id: String) extends fr.ElementId[Page] {
  import Pages._
  override val path = fr.Path(ID_BASE_PATH, id)
}

sealed trait Page extends fr.Element[Page] {
  val id: PageId
}

case class MusicPage(
    id: PageId,
    music: List[fr.AlbumId | fr.SongId],
    linkedTo: Seq[fr.Id[?]],
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  override def errored(): MusicPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): MusicPage = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMusicPage(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processMusicPage(this)
  }

}

case class ShowsPage(
    id: PageId,
    shows: List[fr.ShowId | fr.TourId],
    linkedTo: Seq[fr.Id[?]],
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  override def errored(): ShowsPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): ShowsPage = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processShowsPage(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processShowsPage(this)
  }

}

case class ChronologyPage(
    id: PageId,
    chronology: fr.Chronology,
    hasError: Boolean = false,
) extends Page
    with WithProcessor {
  val linkedTo: Seq[fr.Id[?]] = Nil
  override def errored(): ChronologyPage = copy(hasError = true)
  override def withLinkedTo(id: fr.Id[?]*): ShowsPage = ???

  override def process[T](processor: Processor[T]): T =
    processor.processChronologyPage(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processChronologyPage(this)
  }

}

object ChronologyPage {
  val MARKER_DESIGNATION = "marker"
}

object Pages {
  val ID_BASE_PATH = "page"
}
