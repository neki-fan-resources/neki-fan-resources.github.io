package org.skyluc.neki.data

trait Id {
  def s: String
  override def toString(): String = s
}

trait Item

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
}

object Date {
  final val SEPARATOR = '-'
  final val ZERO = '0'
}

case class Credits(
  lyricist: String,
  composer: String,
  source: Option[Source],
)

case class Source(
  description: String,
)

