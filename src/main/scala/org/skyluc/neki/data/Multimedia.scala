package org.skyluc.neki.data

import java.nio.file.Path

trait MultiMediaId extends Id[MultiMedia]

trait MultiMedia extends Item[MultiMedia] {
  val publishedDate: Date
}

case class YouTubeVideoId(
    id: String
) extends MultiMediaId {
  import YouTubeVideo._
  override val uid: String = ID_BASE + id

  override val upath: String = ID_BASE_UPATH + id + Id.PATH_SEPARATOR

  override def path: Path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = ???

}

case class YouTubeVideo(
    id: YouTubeVideoId,
    label: String,
    publishedDate: Date,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends MultiMedia {

  import YouTubeVideo._

  override def withRelatedTo(id: Id[?]): YouTubeVideo = copy(relatedTo = id :: relatedTo)

  override def errored(): YouTubeVideo = copy(error = true)

  def url(): String = BASE_URL + id.id
  def imageUrl(): String = BASE_IMAGE_URL_1 + id.id + BASE_IMAGE_URL_2

}

object YouTubeVideo {
  val ID_BASE = "youtubevideo_"
  val ID_BASE_UPATH = "youtubevideo/"
  val ID_BASE_PATH = Path.of("youtubevideo")

  val BASE_URL = "https://www.youtube.com/watch?v="
  val BASE_IMAGE_URL_1 = "https://i.ytimg.com/vi/"
  val BASE_IMAGE_URL_2 = "/mqdefault.jpg"

  val OVERLAY_FILE = "youtube.svg"
}

case class MultiMediaBlock(
    video: List[MultiMediaId] = Nil,
    live: List[MultiMediaId] = Nil,
    additional: List[MultiMediaId] = Nil,
)

object MultiMediaBlock {
  val EMPTY = MultiMediaBlock()
}
