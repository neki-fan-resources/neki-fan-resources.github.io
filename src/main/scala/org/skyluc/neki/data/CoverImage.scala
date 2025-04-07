package org.skyluc.neki.data

sealed trait CoverImage

case class FileCoverImage(
    filename: String,
    source: Source,
) extends CoverImage

case class AlbumCoverImage(
    albumId: AlbumId
) extends CoverImage

case class TourCoverImage(
    tourId: TourId
) extends CoverImage

case class SongCoverImage(
    songId: SongId
) extends CoverImage
