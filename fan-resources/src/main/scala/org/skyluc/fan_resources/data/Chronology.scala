package org.skyluc.fan_resources.data

case class Chronology(
    markers: List[ChronologyMarker],
    startDate: Date,
    endDate: Date,
)

trait ChronologyMarkerId extends Id[ChronologyMarker]

trait ChronologyMarker extends Datum[ChronologyMarker] {

  override val hasError: Boolean = false
  override def errored(): ChronologyMarker = this
  // TODO: implement bellow
  override val linkedTo: Seq[Id[?]] = Nil
  override def withLinkedTo(id: Id[?]*): ChronologyMarker = this
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
    image: String,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
) extends ChronologyMarker {

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
) extends ChronologyMarker {

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
) extends ChronologyMarker {

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
) extends ChronologyMarker {

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
) extends ChronologyMarker {

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
) extends ChronologyMarker {

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
