package org.skyluc.neki.data

import java.nio.file.Path

case class AlbumId(id: String) extends Id {
  import Album._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)
}

case class Album(
    id: AlbumId,
    fullname: String,
    altname: Option[String],
    designation: String,
    releaseDate: Date,
    coverImage: CoverImage,
    songs: List[SongId],
    error: Boolean = false,
) extends Item[Album]
    with WithCoverImage[Album] {
  override def errored(): Album = copy(error = true)
}

object Album {
  val ID_BASE = "album_"
  val ID_BASE_UPATH = "album/"
  val ID_BASE_PATH = Path.of("album")

  val URL_BASE = "/album/"
}
