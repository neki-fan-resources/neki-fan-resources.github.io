package org.skyluc.neki.data

import java.nio.file.Path
import java.util.GregorianCalendar
import java.util.Calendar
import scala.collection.mutable.ListBuffer

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

  private def gc(): GregorianCalendar = GregorianCalendar(year, month - 1, day)

  def epochDay(): Int = {
    (gc().getTimeInMillis() / 86400000).toInt
  }

  def fromRefDay(refDay: Int): Int = {
    epochDay() - refDay
  }
}

object Date {

  case class DateTick (
    day: Int,
    label: String,
  )

  def monthIntervals(from: Date, to: Date, refDay: Int): List[DateTick] = {
    tickIntervals(from, to, refDay, Calendar.MONTH, List(Calendar.DAY_OF_MONTH))(d => MONTH_LABELS(d.get(Calendar.MONTH)))
  }

  def yearIntervals(from: Date, to: Date, refDay: Int): List[DateTick] = {
    tickIntervals(from, to, refDay, Calendar.YEAR, List(Calendar.MONTH, Calendar.DAY_OF_MONTH))(d => d.get(Calendar.YEAR).toString())
  }

  def tickIntervals(from: Date, to: Date, refDay: Int, field: Int, zeroFields: List[Int])(label: (GregorianCalendar) => String): List[DateTick] ={
    val fromDate = from.gc()
    val walkingDate = from.gc()
    zeroFields.foreach{ f =>
      walkingDate.set(f, walkingDate.getActualMinimum(f))
    }
    if (!fromDate.equals(walkingDate)) walkingDate.add(field, 1)

    val endDate = to.gc()

    val buffer = ListBuffer[DateTick]()

    while (walkingDate.compareTo(endDate) <= 0) {
      buffer.addOne(DateTick(fromGC(walkingDate).fromRefDay(refDay), label(walkingDate)))
      walkingDate.add(field, 1)
    }

    buffer.toList 
  }

  private val MONTH_LABELS = Array[String]("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

  private def fromGC(date: GregorianCalendar): Date = {
    Date(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH))
  }
  
  given  Ordering[Date] {
    override def compare(x: Date, y: Date): Int = {
      val yearDiff = x.year - y.year
      if (yearDiff != 0) {
        yearDiff
      } else {
        val monthDiff = x.month - y.month
        if (monthDiff != 0) {
          monthDiff
        } else {
          x.day - y.day
        }
      }
    }
  }

  final val SEPARATOR = '-'
  final val ZERO = '0'
}

case class Credits(
    lyricist: String,
    composer: String,
    source: Option[Source],
)

case class Source(
    description: String
)
