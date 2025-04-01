package org.skyluc.neki.data

import java.nio.file.Path

case class PageId[T](id: String) extends Id[T] {
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

trait Page[T] extends Item[T] {
  val id: Id[T]
  val error: Boolean
}

case class MusicPage(
    id: PageId[MusicPage],
    music: List[AlbumId | SongId],
    error: Boolean = false,
) extends Page[MusicPage] {
  override def errored(): MusicPage = copy(error = true)
  override def withRelatedTo(id: Id[?]): MusicPage = ???

}

case class ShowsPage(
    id: PageId[ShowsPage],
    shows: List[ShowId | TourId],
    error: Boolean = false,
) extends Page[ShowsPage] {
  override def errored(): ShowsPage = copy(error = true)
  override def withRelatedTo(id: Id[?]): ShowsPage = ???
}

object Pages {
  val ID_BASE = "page_"
  val ID_BASE_UPATH = "page/"
  val ID_BASE_PATH = Path.of("page")

  val URL_BASE = "/"
}
