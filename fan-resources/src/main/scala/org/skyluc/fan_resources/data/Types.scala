package org.skyluc.fan_resources.data

import org.skyluc.fan_resources.Common

import java.nio.file.Path as fPath

case class Path(
    segments: List[String]
) {
  import Path._

  def asUid(): String = {
    segments.mkString(ID_SEPARATOR)
  }
  def asFilePath(): fPath = {
    segments match {
      case head :: tail =>
        fPath.of(head, tail*)
      case Nil =>
        fPath.of(Common.EMPTY)
    }
  }

  /** @return
    *   the first segment of this path, or "" if empty
    */
  def firstSegment(): String = {
    segments.headOption.getOrElse("")
  }

  def withExtension(extension: String): Path = {
    if (segments.isEmpty) {
      this
    } else {
      Path(segments.init :+ (segments.last + `extension`))
    }
  }

  def insertSecond(segment: String): Path = {
    segments match {
      case head :: tail =>
        Path(head :: segment :: tail)
      case Nil =>
        Path(List(segment))
    }
  }

  def resolve(segment: String): Path = {
    Path(segments :+ segment)
  }

  def resolve(path: Path): Path = {
    Path(segments ++ path.segments)
  }
}

object Path {
  val ID_SEPARATOR = "_"
  def apply(segments: String*): Path = Path(segments.toList)
}

trait WithProcessor {

  def process[T](processor: Processor[T]): T
  def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A]

}

trait Id[T <: Datum[T]] {
  val path: Path

  def uId(): String = path.asUid()

  override def toString(): String = uId()
}

object Id {
  val UNDEFINED = new Id[Nothing] {
    override val path: Path = Path("UNDEFINED")
  }
}

// a piece of data in the data set
trait Datum[T <: Datum[T]] extends WithProcessor {
  val id: Id[T]
  val hasError: Boolean
  val linkedTo: Seq[Id[?]]
  def withLinkedTo(id: Id[?]*): T
  def errored(): T

  protected def mergeLinkedTo(ids: Seq[Id[?]]): Seq[Id[?]] = {
    val newIds = ids.filterNot(id => linkedTo.contains(id))
    linkedTo ++ newIds
  }

  def withLinkedToT(id: Id[?]*): Datum[T] = withLinkedTo(id*)
}

trait ItemId[T <: Item[T]] extends Id[T] {}

// a specific item in data set, might not have its own webpage (multimedia, posts, ..)
trait Item[T <: Item[T]] extends Datum[T] {
  val id: ItemId[T]
}

trait ElementId[T <: Element[T]] extends ItemId[T] {}

// a specific element in the data set, has its own webpage (usually: song, albums, shows, media, ...)
trait Element[T <: Element[T]] extends Item[T] {
  val id: ElementId[T]
}

trait WithMultimedia extends Datum[?] {
  val multimedia: MultiMediaBlock
}

case class Credits(
    lyricist: String,
    composer: String,
    source: Option[Source],
)

case class Source(
    description: String,
    url: Option[String],
)

case class Lyrics(
    status: CriptionLationStatus,
    languages: List[LyricsLanguage],
    sections: List[LyricsSection],
)

case class LyricsLanguage(
    id: String,
    name: String,
    details: Option[String],
    baseurl: Option[String],
    urltext: Option[String],
    active: Boolean = false,
    fixed: Boolean = false,
    notranslation: Boolean = false,
    source: Option[Source],
)

case class LyricsLineEntry(
    w: Option[String],
    d: Option[String],
)

case class LyricsSection(
    lines: List[Map[String, List[LyricsLineEntry]]]
)

case class CriptionLationStatus(
    code: String,
    description: String,
)

case class Summary(
    status: CriptionLationStatus,
    items: List[SummaryItem],
)

case class SummaryItem(
    label: String,
    sub: List[SummaryItem],
)

case class RefMediaIds(
    account: String,
    ids: List[String],
)
