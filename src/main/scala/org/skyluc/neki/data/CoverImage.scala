package org.skyluc.neki.data

trait CoverImage

case class FileCoverImage(
  filename: String,
  source: Source
) extends CoverImage

