package org.skyluc.fan_resources.data

case class SongId(id: String, dark: Boolean = false) extends ElementId[Song] {
  import Song._

  override val path = Path(ID_BASE_PATH, id)
}

case class Song(
    id: SongId,
    fullname: String,
    fullnameEn: Option[String],
    album: Option[AlbumId],
    releaseDate: Date,
    description: Option[List[String]],
    credits: Option[Credits],
    coverImage: CoverImage,
    multimedia: MultiMediaBlock,
    lyrics: Option[Lyrics],
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Element[Song] {

  override def process[T](processor: Processor[T]): T =
    processor.processSong(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processSong(this)
  }

  override def errored(): Song = copy(hasError = true)
  override def withLinkedTo(id: Id[?]*): Song = copy(linkedTo = mergeLinkedTo(id))
}

object Song {
  val ID_BASE_PATH = "song"

  val FROM_KEY = "song"
}
