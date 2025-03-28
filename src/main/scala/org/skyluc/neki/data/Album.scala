package org.skyluc.neki.data

case class AlbumId(id: String) extends Id {
  def s = Album.ID_BASE + id
}

case class Album(
  id: AlbumId,
  fullname: String,
  altname: Option[String],
  designation: String,
  releaseDate: Date,
) extends Item

object Album {
  final val ID_BASE = "album_"
}