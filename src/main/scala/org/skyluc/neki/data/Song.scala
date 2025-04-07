package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.html.page.SourcesPage.SourceItem

case class SongId(id: String, dark: Boolean = false) extends Id[Song] {
  import Song._
  override val uid = (if (dark) "d_" else "") + ID_BASE + id
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
    lyrics: Option[Lyrics],
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

  def sources(): Option[SourceItem] = {
    val s = List(
      coverImage.sourceEntry(),
      credits.flatMap(_.sourceEntry()),
    ).flatten ::: lyrics.map(_.sourceEntries()).getOrElse(Nil)
    if (s.isEmpty) {
      None
    } else {
      Some(SourceItem(fullname, s))
    }
  }

}

object Song {
  val ID_BASE = "song_"
  val ID_BASE_UPATH = "song/"
  val ID_BASE_PATH = Path.of("song")

  val URL_BASE = "/song/"
  val URL_BASE_DARK = "/dark/song/"
  val FROM_KEY = "song"
}
