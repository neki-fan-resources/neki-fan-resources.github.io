package org.skyluc.neki.data

import java.nio.file.Path

case class SongId(id: String) extends Id[Song] {
  import Song._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.songs.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced song '$id' is not found"))
    }
  }
}

case class Song(
    id: SongId,
    fullname: String,
    fullnameEn: Option[String],
    album: Option[AlbumId],
    releaseDate: Date,
    credits: Option[Credits],
    coverImage: CoverImage,
    multimedia: MultiMediaBlock,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Item[Song]
    with WithCoverImage[Song] {
  override def errored(): Song = copy(error = true)
  override def withRelatedTo(id: Id[?]): Song = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

}

object Song {
  val ID_BASE = "song_"
  val ID_BASE_UPATH = "song/"
  val ID_BASE_PATH = Path.of("song")

  val URL_BASE = "/song/"
  val FROM_KEY = "song"
}
