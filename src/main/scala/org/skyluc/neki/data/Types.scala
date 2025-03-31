package org.skyluc.neki.data

import java.nio.file.Path

trait Id {
  val uid: String
  val upath: String
  def path: Path
  override def toString(): String = uid
}

object Id {
  val PATH_SEPARATOR = "/"
  val ID_SEPARATOR = "_"
}

trait WithErrorSupport[T] {
  def errored(): T
}

trait Item[T] extends WithErrorSupport[T] {
  val id: Id
  val error: Boolean
}

trait WithCoverImage[T] extends WithErrorSupport[T] {
  val id: Id
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
    description: String
)
