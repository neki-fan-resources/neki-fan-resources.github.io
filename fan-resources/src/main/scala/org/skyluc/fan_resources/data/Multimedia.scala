package org.skyluc.fan_resources.data

trait MultiMediaId extends ItemId[MultiMedia]

trait MultiMedia extends Item[MultiMedia] {

  val publishedDate: Date
}

case class LocalImageId(
    itemId: ItemId[?],
    id: String,
) extends MultiMediaId {

  import LocalImage._

  override val path: Path = Path(ID_BASE_PATH, itemId.uId(), id)
}

case class LocalImage(
    id: LocalImageId,
    filename: String,
    label: String,
    publishedDate: Date,
    linkedTo: Seq[Id[?]] = Nil,
    hasError: Boolean = false,
) extends MultiMedia {

  override def process[T](processor: Processor[T]): T =
    processor.processLocalImage(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processLocalImage(this)
  }

  override def withLinkedTo(id: Id[?]*): LocalImage = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): LocalImage = copy(hasError = true)
}

object LocalImage {
  val ID_BASE_PATH = "localimage"

  val DESIGNATION = "image"
}

case class PostXImageId(
    postId: String,
    imageId: String,
) extends MultiMediaId {

  import PostXImageId._

  override val path: Path = Path(ID_BASE_PATH, postId, imageId)
}

object PostXImageId {
  val ID_BASE_PATH = "postximage"

}

case class YouTubeVideoId(
    id: String
) extends MultiMediaId {
  import YouTubeVideo._

  override val path: Path = Path(ID_BASE_PATH, id)
}

case class YouTubeVideo(
    id: YouTubeVideoId,
    label: String,
    publishedDate: Date,
    linkedTo: Seq[Id[?]] = Nil, // TODO: using append, should not use List
    hasError: Boolean = false,
) extends MultiMedia {

  import YouTubeVideo._

  override def process[T](processor: Processor[T]): T =
    processor.processYouTubeVideo(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processYouTubeVideo(this)
  }

  override def withLinkedTo(id: Id[?]*): YouTubeVideo = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): YouTubeVideo = copy(hasError = true)
}

object YouTubeVideo {
  val ID_BASE_PATH = "youtubevideo"

  val BASE_URL = "https://www.youtube.com/watch?v="
  val BASE_IMAGE_URL_1 = "https://i.ytimg.com/vi/"
  val BASE_IMAGE_URL_2 = "/mqdefault.jpg"

  val OVERLAY_FILE = "youtube.svg"
}

case class YouTubeShortId(id: String) extends MultiMediaId {

  import YouTubeShort._

  override val path: Path = Path(ID_BASE_PATH, id)
}

case class YouTubeShort(
    id: YouTubeShortId,
    label: String,
    info: Option[String],
    publishedDate: Date,
    linkedTo: Seq[Id[?]] = Nil,
    hasError: Boolean = false,
) extends MultiMedia {

  import YouTubeShort._

  override def process[T](processor: Processor[T]): T =
    processor.processYouTubeShort(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processYouTubeShort(this)
  }

  override def withLinkedTo(id: Id[?]*): MultiMedia = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): MultiMedia = copy(hasError = true)

  def url(): String = BASE_URL + id.id
  def imageUrl(): String = BASE_IMAGE_URL_1 + id.id + BASE_IMAGE_URL_2
}

object YouTubeShort {
  val ID_BASE_PATH = "youtubeshort"

  val BASE_URL = "https://www.youtube.com/shorts/"
  val BASE_IMAGE_URL_1 = "https://i.ytimg.com/vi/"
  val BASE_IMAGE_URL_2 = "/oardefault.jpg"

}

case class ZaikoId(
    channel: String,
    id: String,
) extends MultiMediaId {
  import Zaiko._

  override val path: Path = Path(ID_BASE_PATH, channel, id)
}

case class Zaiko(
    id: ZaikoId,
    label: String,
    coverImage: String,
    publishedDate: Date,
    expirationDate: Option[Date],
    linkedTo: Seq[Id[?]] = Nil,
    hasError: Boolean = false,
) extends MultiMedia {
  import Zaiko._

  override def process[T](processor: Processor[T]): T =
    processor.processZaiko(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processZaiko(this)
  }

  override def withLinkedTo(id: Id[?]*): MultiMedia = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): MultiMedia = copy(hasError = true)

  def url(): String = BASE_URL_1 + id.channel + BASE_URL_2 + id.id
}

object Zaiko {
  val ID_BASE_PATH = "zaiko"

  val DESIGNATION = "zaiko"

  val BASE_URL_1 = "https://"
  val BASE_URL_2 = ".zaiko.io/item/"
  val OVERLAY_FILE = "zaiko.ico"
}

case class MultiMediaBlock(
    video: List[MultiMediaId] = Nil,
    live: List[MultiMediaId] = Nil,
    concert: List[MultiMediaId] = Nil,
    short: List[MultiMediaId] = Nil,
    image: List[MultiMediaId] = Nil,
    additional: List[MultiMediaId] = Nil,
) {
  def all(): List[MultiMediaId] = (video ::: live ::: concert ::: short ::: image ::: additional).distinct

  def extra(relatedTo: Seq[Id[?]]): Seq[MultiMediaId] = {
    val allMultiMediaInMainPage = all()
    val allRelatedMultiMedia: Seq[MultiMediaId] = relatedTo.flatMap {
      case m: MultiMediaId =>
        Some(m)
      case _ =>
        None
    }
    allRelatedMultiMedia.filterNot(allMultiMediaInMainPage.contains(_))
  }
}

object MultiMediaBlock {
  val EMPTY = MultiMediaBlock()
}
