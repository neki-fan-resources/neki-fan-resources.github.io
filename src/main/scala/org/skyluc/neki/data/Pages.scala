package org.skyluc.neki.data

import java.nio.file.Path

case class PageId(id: String) extends Id[Page] {
  import Pages._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.pages.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced page '$id' is not found"))
    }
  }
}

sealed trait Page extends Item[Page] {
  val id: Id[Page]
  val error: Boolean
  val relatedTo: List[Id[?]] = Nil
}

case class MusicPage(
    id: PageId,
    music: List[AlbumId | SongId],
    error: Boolean = false,
) extends Page {
  override def errored(): MusicPage = copy(error = true)
  override def withRelatedTo(id: Id[?]): MusicPage = ???

}

case class ShowsPage(
    id: PageId,
    shows: List[ShowId | TourId],
    error: Boolean = false,
) extends Page {
  override def errored(): ShowsPage = copy(error = true)
  override def withRelatedTo(id: Id[?]): ShowsPage = ???
}

case class ChronologyPage(
    id: PageId,
    chronology: Chronology,
    error: Boolean = false,
) extends Page {
  override def errored(): ChronologyPage = copy(error = true)
  override def withRelatedTo(id: Id[?]): ShowsPage = ???
}

object Pages {
  val ID_BASE = "page_"
  val ID_BASE_UPATH = "page/"
  val ID_BASE_PATH = Path.of("page")

  val URL_BASE = "/"
}
