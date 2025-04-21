package org.skyluc.fan_resources.data

trait PostId extends ItemId[Post] {}

trait Post extends Item[Post] {}

case class PostXId(
    id: String
) extends PostId {

  import PostX._

  override val path: Path = Path(ID_BASE_PATH, id)
}

case class PostX(
    id: PostXId,
    account: String,
    publishedDate: Date,
    info: String,
    text: String,
    image: List[PostXImage],
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Post {

  import PostX._

  def url(): String = {
    POST_BASE_URL_1 + account + POST_BASE_URL_2 + id.id
  }

  override def process[T](processor: Processor[T]): T =
    processor.processPostX(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processPostX(this)
  }

  override def withLinkedTo(id: Id[?]*): PostX = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): PostX = copy(hasError = false)
}

case class PostXImage(
    id: PostXImageId,
    label: String,
    publishedDate: Date,
    info: Option[String],
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends MultiMedia {

  import PostX._

  def url(): String = {
    IMAGE_BASE_URL_1 + id + IMAGE_BASE_URL_2
  }

  override def process[T](processor: Processor[T]): T =
    processor.processPostXImage(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processPostXImage(this)
  }

  override def withLinkedTo(id: Id[?]*): PostXImage = copy(linkedTo = mergeLinkedTo(id))

  override def errored(): PostXImage = copy(hasError = false)
}

object PostX {
  val ID_BASE_PATH = "xpost"

  val FROM_KEY = "xpost"

  // TODO: this should not be in the fan_resources section
  val DESIGNATION = "X post"
  val DESIGNATION_IMAGE = "X post image"

  val POST_BASE_URL_1 = "https://x.com/"
  val POST_BASE_URL_2 = "/status/"
  val IMAGE_BASE_URL_1 = "https://pbs.twimg.com/media/"
  val IMAGE_BASE_URL_2 = "?format=png&name=small"
}
