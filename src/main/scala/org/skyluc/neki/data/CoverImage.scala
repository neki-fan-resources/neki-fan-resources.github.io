package org.skyluc.neki.data

import org.skyluc.neki.html.page.SourcesPage.SourceEntry

sealed trait CoverImage {
  def sourceEntry(): Option[SourceEntry] = None
}

case class FileCoverImage(
    filename: String,
    source: Source,
) extends CoverImage {
  override def sourceEntry(): Option[SourceEntry] = Some(SourceEntry("Cover image", source.description, source.url))
}

case class AlbumCoverImage(
    albumId: AlbumId
) extends CoverImage

case class TourCoverImage(
    tourId: TourId
) extends CoverImage

case class SongCoverImage(
    songId: SongId
) extends CoverImage
