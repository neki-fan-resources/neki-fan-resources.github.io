package org.skyluc.neki.data

import java.nio.file.Path

case class PageId(id: String) extends Id {
  import Pages._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)
}

trait Page {
  val id: PageId
  val error: Boolean
}

case class MusicPage(
    id: PageId,
    music: List[AlbumId | SongId],
    error: Boolean = false,
) extends Item[MusicPage]
    with Page {
  override def errored(): MusicPage = copy(error = true)
}

case class ShowsPage(
    id: PageId,
    shows: List[ShowId | TourId],
    error: Boolean = false,
) extends Item[ShowsPage]
    with Page {
  override def errored(): ShowsPage = copy(error = true)
}

object Pages {
  val ID_BASE = "page_"
  val ID_BASE_UPATH = "page/"
  val ID_BASE_PATH = Path.of("page")

  val URL_BASE = "/"
}
