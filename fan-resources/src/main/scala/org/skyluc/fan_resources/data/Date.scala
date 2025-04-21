package org.skyluc.fan_resources.data

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import scala.annotation.tailrec
import scala.util.matching.Regex

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

  final val DATE_PATTERN: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

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

  private def tickIntervals(from: Date, to: Date, refDay: Int, years: Boolean)(label: (ZonedDateTime) => String): List[DateTick] ={

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
