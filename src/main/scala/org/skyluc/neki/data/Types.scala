package org.skyluc.neki.data

trait Id {
  def s: String
  override def toString(): String = s
}

trait Item

case class Date(year: Int, month: Int, day: Int)

case class Credits(
  lyricist: String,
  composer: String,
  source: Option[Source],
)

case class Source(
  description: String,
)

