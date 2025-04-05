package org.skyluc.neki.data

import java.nio.file.Path

case class AlbumId(id: String) extends Id[Album] {
  import Album._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.albums.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced album '$id' is not found"))
    }
  }
}

case class Album(
    id: AlbumId,
    fullname: String,
    altname: Option[String],
    designation: String,
    releaseDate: Date,
    coverImage: CoverImage,
    songs: List[SongId],
    multimedia: MultiMediaBlock,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Item[Album]
    with WithCoverImage[Album] {
  override def errored(): Album = copy(error = true)

  override def withRelatedTo(id: Id[?]): Album = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }
}

object Album {
  val ID_BASE = "album_"
  val ID_BASE_UPATH = "album/"
  val ID_BASE_PATH = Path.of("album")

  val URL_BASE = "/album/"

  val FROM_KEY = "album"
}
