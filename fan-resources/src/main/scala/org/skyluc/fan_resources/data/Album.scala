package org.skyluc.fan_resources.data

case class AlbumId(id: String) extends ElementId[Album] {
  import Album._
  override val path = Path(ID_BASE_PATH, id)
}

case class Album(
    id: AlbumId,
    fullname: String,
    altname: Option[String],
    designation: String,
    description: Option[List[String]],
    releaseDate: Date,
    coverImage: CoverImage,
    songs: List[SongId],
    multimedia: MultiMediaBlock,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Element[Album] {
  override def errored(): Album = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): Album = {
    copy(linkedTo = mergeLinkedTo(id))
  }

  override def process[T](processor: Processor[T]): T = {
    processor.processAlbum(this)
  }

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processAlbum(this)
  }
}

object Album {
  val ID_BASE_PATH = "album"

  val FROM_KEY = "album"
}
