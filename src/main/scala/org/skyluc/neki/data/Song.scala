package org.skyluc.neki.data

import java.nio.file.Path

case class SongId(id: String) extends Id {
  import Song._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)
}

case class Song(
    id: SongId,
    fullname: String,
    fullnameEn: Option[String],
    album: Option[AlbumId],
    releaseDate: Date,
    credits: Option[Credits],
    coverImage: CoverImage,
    error: Boolean = false,
) extends Item[Song]
    with WithCoverImage[Song] {
  override def errored(): Song = copy(error = true)
}

object Song {
  val ID_BASE = "song_"
  val ID_BASE_UPATH = "song/"
  val ID_BASE_PATH = Path.of("song")

  val URL_BASE = "/song/"
}
