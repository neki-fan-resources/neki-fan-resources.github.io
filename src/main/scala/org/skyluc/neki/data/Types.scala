package org.skyluc.neki.data

import java.nio.file.Path
import java.time.temporal.ChronoField
import java.time.ZonedDateTime
import java.time.ZoneOffset
import scala.annotation.tailrec
import org.skyluc.neki.html.page.SourcesPage.SourceEntry

trait Id[T] {
  val uid: String
  val upath: String
  def path: Path
  def isKnown(sourceId: Id[?], data: Data): Option[DataError]
  override def toString(): String = uid
}

object Id {
  val PATH_SEPARATOR = "/"
  val ID_SEPARATOR = "_"
}

trait WithErrorSupport[T] {
  def errored(): T
}

trait Item[T <: Item[T]] extends WithErrorSupport[T] {
  val id: Id[T]
  val error: Boolean
  val relatedTo: List[Id[?]]
  def withRelatedTo(id: Id[?]): T
  def withRelatedToGen(id: Id[?]): Item[T] = withRelatedTo(id)
}

trait WithCoverImage[T] extends WithErrorSupport[T] {
  val id: Id[T]
  val coverImage: CoverImage
}

case class Date(year: Int, month: Int, day: Int) {
  import Date._
  override def toString(): String = {

    val stringBuilder = new StringBuilder(10)
      .append(year)
      .append(SEPARATOR)
    if (month < 10) {
      stringBuilder.append(ZERO)
    }
    stringBuilder
      .append(month)
      .append(SEPARATOR)
    if (day < 10) {
      stringBuilder.append(ZERO)
    }
    stringBuilder
      .append(day)
      .toString()
  }

  def toStringSafe(): String = s"${year}_${month}_${day}"

  private def dt(): ZonedDateTime = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC)

  def epochDay(): Int = dt().getLong(ChronoField.EPOCH_DAY).toInt

  def fromRefDay(refDay: Int): Int = {
    epochDay() - refDay
  }

  def isPast(): Boolean = {
    comparedTo(today) < 0
  }

  def comparedTo(other: Date): Int = {
    val yearDiff = year - other.year
    if (yearDiff != 0) {
      yearDiff
    } else {
      val monthDiff = month - other.month
      if (monthDiff != 0) {
        monthDiff
      } else {
        day - other.day
      }
    }
  }
}

object Date {

  val today = fromDateTime(ZonedDateTime.now(ZoneOffset.UTC))

  case class DateTick (
    day: Int,
    label: String,
  )

  def monthIntervals(from: Date, to: Date, refDay: Int): List[DateTick] = {
    tickIntervals(from, to, refDay, false)(d => MONTH_LABELS(d.getMonthValue()))
  }

  def yearIntervals(from: Date, to: Date, refDay: Int): List[DateTick] = {
    tickIntervals(from, to, refDay, true)(d => d.getYear().toString())
  }

  def tickIntervals(from: Date, to: Date, refDay: Int, years: Boolean)(label: (ZonedDateTime) => String): List[DateTick] ={

    val fromDate = from.dt()
    val toDate = to.dt()
    val beginDateCandidate = if (years) {
      fromDate.withMonth(1).withDayOfMonth(1)
    } else {
      fromDate.withDayOfMonth(1)
    }
    val beginDate = if (fromDate == beginDateCandidate) {
      beginDateCandidate
    } else {
      if (years) {
        beginDateCandidate.plusYears(1)
      } else {
        beginDateCandidate.plusMonths(1)
      }
    }

    @tailrec def loop(current: ZonedDateTime, acc: List[ZonedDateTime]): List[ZonedDateTime] = {
      if (current.compareTo(toDate) <= 0) {
        val next = if (years) {
          current.plusYears(1)
        } else {
          current.plusMonths(1)
        }
        loop(next, current :: acc)
      } else {
        acc.reverse
      }
    }

    loop(beginDate, Nil).map(d => DateTick(fromDateTime(d).fromRefDay(refDay), label(d)))
  }

  private val MONTH_LABELS = Array[String]("one-based", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

  private def fromDateTime(date: ZonedDateTime): Date = {
    Date(date.getYear(), date.getMonthValue(), date.getDayOfMonth())
  }
  
  given  Ordering[Date] {
    override def compare(x: Date, y: Date): Int = {
      x.comparedTo(y)
    }
  }

  final val SEPARATOR = '-'
  final val ZERO = '0'
}

case class Credits(
    lyricist: String,
    composer: String,
    source: Option[Source],
) {
  def sourceEntry(): Option[SourceEntry] = 
    source.map(s => SourceEntry("Credits", s.description, s.url))
}

case class Source(
    description: String,
    url: Option[String],
)

case class Lyrics(
  status: CriptionLationStatus,
  languages: List[LyricsLanguage],
  sections: List[LyricsSection],
) {
  def sourceEntries(): List[SourceEntry] = {
    languages.flatMap{l =>
      l.source.map(s => SourceEntry(l.name, s.description, s.url))
    }
  }
}

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

case class BandNews(
    title: String,
    content: List[String],
    url: String,
)

case class Summary(
  status: CriptionLationStatus,
  items: List[SummaryItem],
)

case class SummaryItem(
  label: String,
  sub: List[SummaryItem],
)