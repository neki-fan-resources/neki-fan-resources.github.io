package org.skyluc.fan_resources.data

case class MediaId(year: String, id: String) extends ElementId[Media] {

  import Media._

  override val path: Path = Path(ID_BASE_PATH, year, id)

}

trait Media extends Element[Media] {
  val id: MediaId
  val publishedDate: Date
  def title(): String
  val summary: Option[Summary]
  val multimedia: MultiMediaBlock
  override def errored(): Media
  override def withLinkedTo(id: Id[?]*): Media
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
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Media {

  override def title(): String = radio + " - " + show

  override def errored(): MediaAudio = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): MediaAudio = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMediaAudio(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processMediaAudio(this)
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
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Media {
  override def title(): String = publication + " - " + issue

  override def errored(): MediaWritten = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): MediaWritten = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMediaWritten(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processMediaWritten(this)
  }
}

object Media {
  val ID_BASE_PATH = "media"

  val FROM_KEY = "media"
}
