package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.html.page.SourcesPage.SourceItem

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

trait Media extends Item[Media] with WithCoverImage[Media] {
  val id: MediaId
  val publishedDate: Date
  def title(): String
  val summary: Option[Summary]
  val multimedia: MultiMediaBlock
  def sources(): Option[SourceItem]
}

case class MediaAudio(
    id: MediaId,
    radio: String,
    show: String,
    designation: String,
    program: Option[String],
    host: String,
    member: List[String],
    webpage: Option[String],
    publishedDate: Date,
    description: Option[List[String]],
    coverImage: CoverImage,
    summary: Option[Summary],
    multimedia: MultiMediaBlock,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Media {

  override def title(): String = radio + " - " + show

  override def errored(): MediaAudio = copy(error = true)

  override def withRelatedTo(id: Id[?]): MediaAudio = copy(relatedTo = id :: relatedTo)

  def sources(): Option[SourceItem] = {
    coverImage.sourceEntry().map(s => SourceItem(title(), List(s)))
  }
}

case class MediaWritten(
    id: MediaId,
    publication: String,
    issue: String,
    designation: String,
    journalist: Option[String],
    member: List[String],
    articlePage: Option[String],
    webpage: Option[String],
    publishedDate: Date,
    description: Option[List[String]],
    coverImage: CoverImage,
    summary: Option[Summary],
    multimedia: MultiMediaBlock,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Media {
  override def title(): String = publication + " - " + issue

  override def errored(): MediaWritten = copy(error = true)

  override def withRelatedTo(id: Id[?]): MediaWritten = copy(relatedTo = id :: relatedTo)

  def sources(): Option[SourceItem] = {
    coverImage.sourceEntry().map(s => SourceItem(title(), List(s)))
  }
}

object Media {
  val ID_BASE = "media_"
  val ID_BASE_UPATH = "media/"
  val ID_BASE_PATH = Path.of("media")

  val URL_BASE = "/media/"

  val FROM_KEY = "media"
}
