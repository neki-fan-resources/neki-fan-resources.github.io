package org.skyluc.neki.data

import java.nio.file.Path

case class MediaId(year: String, id: String) extends Id[Media] {

  import Media._

  override val uid: String = ID_BASE + year + Id.ID_SEPARATOR + id

  override val upath: String = ID_BASE_UPATH + year + Id.PATH_SEPARATOR + id + Id.PATH_SEPARATOR

  override def path: Path = ID_BASE_PATH.resolve(year).resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.medias.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced media '$year/$id' is not found"))
    }

  }

}

case class Media(
    id: MediaId,
    radio: String,
    show: String,
    program: Option[String],
    host: String,
    member: List[String],
    webpage: Option[String],
    publishedDate: Date,
    coverImage: CoverImage,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Item[Media]
    with WithCoverImage[Media] {

  override def errored(): Media = copy(error = true)

  override def withRelatedTo(id: Id[?]): Media = copy(relatedTo = id :: relatedTo)

}

object Media {
  val ID_BASE = "media_"
  val ID_BASE_UPATH = "media/"
  val ID_BASE_PATH = Path.of("media")

  val URL_BASE = "/media/"

  val FROM_KEY = "media"
}
