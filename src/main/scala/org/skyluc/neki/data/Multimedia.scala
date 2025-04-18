package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.html.CommonBase
import org.skyluc.neki.html.CompiledData

trait MultiMediaId extends Id[MultiMedia]

trait MultiMedia extends Item[MultiMedia] {
  val publishedDate: Date
}

case class LocalImageId(
    itemId: Id[?],
    id: String,
) extends MultiMediaId {

  import LocalImage._

  override val uid: String = ID_BASE + itemId.uid + Id.ID_SEPARATOR + id

  override val upath: String = CommonBase.NOT_USED

  override def path: Path = ???

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.multimedia.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced multimedia '$id' is not found"))
    }
  }
}

case class LocalImage(
    id: LocalImageId,
    filename: String,
    label: String,
    publishedDate: Date,
    relatedTo: List[Id[?]] = Nil,
    error: Boolean = false,
) extends MultiMedia {

  def url(): String = {
    CompiledData.BASE_IMAGE_ASSET + id.itemId.upath + filename
  }

  override def withRelatedTo(id: Id[?]): MultiMedia = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

  override def errored(): MultiMedia = copy(error = true)
}

object LocalImage {
  val ID_BASE = "localimage_"

  val OVERLAY_FILE = "empty.png"
}

case class PostXImageId(
    postId: String,
    imageId: String,
) extends MultiMediaId {

  import PostXImageId._

  override val uid: String = ID_BASE + postId + Id.ID_SEPARATOR + imageId

  override val upath: String = CommonBase.NOT_USED

  override def path: Path = ???

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = ???
}

object PostXImageId {
  val ID_BASE = "postximage_"

}

case class YouTubeVideoId(
    id: String
) extends MultiMediaId {
  import YouTubeVideo._
  override val uid: String = ID_BASE + id

  override val upath: String = ID_BASE_UPATH + id + Id.PATH_SEPARATOR

  override def path: Path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.multimedia.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced multimedia '$id' is not found"))
    }
  }

}

case class YouTubeVideo(
    id: YouTubeVideoId,
    label: String,
    publishedDate: Date,
    relatedTo: List[Id[?]] = Nil, // TODO: using append, should not use List
    error: Boolean = false,
) extends MultiMedia {

  import YouTubeVideo._

  override def withRelatedTo(id: Id[?]): YouTubeVideo = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

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

case class YouTubeShortId(id: String) extends MultiMediaId {

  import YouTubeShort._

  override val uid: String = ID_BASE + id

  override val upath: String = ID_BASE_UPATH + id + Id.PATH_SEPARATOR

  override def path: Path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.multimedia.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced multimedia '$id' is not found"))
    }
  }

}

case class YouTubeShort(
    id: YouTubeShortId,
    label: String,
    info: Option[String],
    publishedDate: Date,
    relatedTo: List[Id[?]] = Nil,
    error: Boolean = false,
) extends MultiMedia {

  import YouTubeShort._

  override def withRelatedTo(id: Id[?]): MultiMedia = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

  override def errored(): MultiMedia = copy(error = true)

  def url(): String = BASE_URL + id.id
  def imageUrl(): String = BASE_IMAGE_URL_1 + id.id + BASE_IMAGE_URL_2
}

object YouTubeShort {
  val ID_BASE = "youtubeshort_"
  val ID_BASE_UPATH = "youtubeshort/"
  val ID_BASE_PATH = Path.of("youtubeshort")

  val BASE_URL = "https://www.youtube.com/shorts/"
  val BASE_IMAGE_URL_1 = "https://i.ytimg.com/vi/"
  val BASE_IMAGE_URL_2 = "/oardefault.jpg"

}

case class ZaikoId(
    channel: String,
    id: String,
) extends MultiMediaId {
  import Zaiko._

  override val uid: String = ID_BASE + channel + ID_SEPARATOR + id

  override val upath: String = ID_BASE_UPATH + channel + ID_SEPARATOR + id + Id.PATH_SEPARATOR

  override def path: Path = ID_BASE_PATH.resolve(channel + ID_SEPARATOR + id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.multimedia.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced multimedia '$id' is not found"))
    }
  }

}

case class Zaiko(
    id: ZaikoId,
    label: String,
    coverImage: String,
    publishedDate: Date,
    expirationDate: Option[Date],
    relatedTo: List[Id[?]] = Nil,
    error: Boolean = false,
) extends MultiMedia {
  import Zaiko._

  override def withRelatedTo(id: Id[?]): MultiMedia = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

  override def errored(): MultiMedia = copy(error = true)

  def url(): String = BASE_URL_1 + id.channel + BASE_URL_2 + id.id
}

object Zaiko {
  val ID_BASE = "zaiko_"
  val ID_SEPARATOR = "_"
  val ID_BASE_UPATH = "zaiko/"
  val ID_BASE_PATH = Path.of("zaiko")

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

  def extra(relatedTo: List[Id[?]]): List[MultiMediaId] = {
    val allMultiMediaInMainPage = all()
    val allRelatedMultiMedia: List[MultiMediaId] = relatedTo.flatMap {
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
