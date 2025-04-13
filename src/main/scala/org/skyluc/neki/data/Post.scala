package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.data.Album.ID_BASE
import org.skyluc.neki.html.CommonBase

trait PostId extends Id[Post] {}

trait Post extends Item[Post] {}

case class PostXId(
    id: String
) extends PostId {

  override val uid: String = ID_BASE + id

  override val upath: String = CommonBase.NOT_USED

  override def path: Path = ???

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = ???
}

case class PostX(
    id: PostXId,
    account: String,
    publishedDate: Date,
    info: String,
    text: String,
    image: List[PostXImage],
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Post {

  import PostX._

  def getImage(imageId: String): Option[PostXImage] = {
    image.find(_.id == imageId)
  }

  def url(): String = {
    POST_BASE_URL_1 + account + POST_BASE_URL_2 + id.id
  }

  override def withRelatedTo(id: Id[?]): Post = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }

  override def errored(): Post = copy(error = false)
}

case class PostXImage(
    id: String,
    label: String,
    info: Option[String],
) {

  import PostX._

  def url(): String = {
    IMAGE_BASE_URL_1 + id + IMAGE_BASE_URL_2
  }
}

object PostX {
  val ID_BASE = "postx_"
  // val ID_BASE_UPATH = "postx/"
  // val ID_BASE_PATH = ???

  val OVERLAY_FILE = "x.ico"

  val POST_BASE_URL_1 = "https://x.com/"
  val POST_BASE_URL_2 = "/status/"
  val IMAGE_BASE_URL_1 = "https://pbs.twimg.com/media/"
  val IMAGE_BASE_URL_2 = "?format=png&name=small"
}
