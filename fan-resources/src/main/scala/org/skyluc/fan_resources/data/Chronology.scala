package org.skyluc.fan_resources.data

case class Chronology(
    markers: List[ChronologyMarker],
    startDate: Date,
    endDate: Date,
)

trait ChronologyMarkerId extends Id[ChronologyMarker]

trait ChronologyMarker extends Datum[ChronologyMarker] {}

object ChronologyMarker {
  val FROM_KEY = "marker"
}

case class BaseMarkerId(id: String) extends ChronologyMarkerId {

  import BaseMarker._

  override val path: Path = ID_BASE_PATH.resolve(id)

}

object BaseMarkerId {

  def cleaned(text: String): BaseMarkerId = {
    val id = text.toLowerCase().filter(c => c >= 'a' && c <= 'z' || c >= '0' && c <= '9')
    BaseMarkerId(id)
  }
}

case class BaseMarker(
    id: BaseMarkerId,
    label: String,
    date: Date,
    // TODO: use local image definition ?
    image: String,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): BaseMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): BaseMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processBaseMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processBaseMarker(this)
}

object BaseMarker {

  val ID_BASE_PATH = Path("marker")
}

case class MediaMarkerId(mediaId: MediaId) extends ChronologyMarkerId {

  import MediaMarker._

  override val path: Path = ID_BASE_PATH.resolve(mediaId.path)

}

case class MediaMarker(
    id: MediaMarkerId,
    short: Boolean,
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): MediaMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): MediaMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMediaMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processMediaMarker(this)
}

object MediaMarker {

  val ID_BASE_PATH = Path("mediamarker")
}

case class ShowMarkerId(showId: ShowId) extends ChronologyMarkerId {
  import ShowMarker._
  override val path: Path = ID_BASE_PATH.resolve(showId.path)
}

case class ShowMarker(
    id: ShowMarkerId,
    short: Boolean,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): ShowMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): ShowMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processShowMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processShowMarker(this)
}

object ShowMarker {

  val ID_BASE_PATH = Path("showmarker")
}

case class SongMarkerId(songId: SongId) extends ChronologyMarkerId {

  import SongMarker._
  override val path: Path = ID_BASE_PATH.resolve(songId.path)
}

case class SongMarker(
    id: SongMarkerId,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): SongMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): SongMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processSongMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processSongMarker(this)
}

object SongMarker {

  val ID_BASE_PATH = Path("songmarker")

  val SUBLABEL = Some("Release")
}

case class AlbumMarkerId(albumId: AlbumId) extends ChronologyMarkerId {

  import AlbumMarker._

  override val path: Path = ID_BASE_PATH.resolve(albumId.path)
}

case class AlbumMarker(
    id: AlbumMarkerId,
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): AlbumMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): AlbumMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processAlbumMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processAlbumMarker(this)
}

object AlbumMarker {

  val ID_BASE_PATH = Path("albummarker")

  val SUBLABEL = Some("Release")
}

case class MultiMediaMarkerId(multimediaId: MultiMediaId) extends ChronologyMarkerId {
  import MultiMediaMarker._

  override val path: Path = ID_BASE_PATH.resolve(multimediaId.path)
}

case class MultiMediaMarker(
    id: MultiMediaMarkerId,
    parentKey: String,
    position: Position,
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends ChronologyMarker {

  override def errored(): MultiMediaMarker = copy(hasError = true)

  override def withLinkedTo(id: Id[?]*): MultiMediaMarker = copy(linkedTo = mergeLinkedTo(id))

  override def process[T](processor: Processor[T]): T =
    processor.processMultiMediaMarker(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] =
    processor.processMultiMediaMarker(this)
}

object MultiMediaMarker {

  val ID_BASE_PATH = Path("multimediamarker")
}

case class Position(
    up: Int,
    in: Int,
)
