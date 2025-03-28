package org.skyluc.neki.data

case class SongId(id: String) extends Id {
  def s = Song.ID_BASE + id
}

case class Song(
  id: SongId,
  fullname: String,
  fullnameEn: Option[String],
  album: Option[AlbumId],
  releaseDate: Date,
  credits: Option[Credits]
) extends Item

object Song {
  final val ID_BASE = "song_"

}