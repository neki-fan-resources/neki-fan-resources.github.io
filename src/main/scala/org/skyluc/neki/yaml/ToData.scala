package org.skyluc.neki.yaml

import org.skyluc.neki.data.{
  Album => dAlbum,
  AlbumId => dAlbumId,
  Credits => dCredits,
  Date => dDate,
  Id => dId,
  Item => dItem,
  Song => dSong,
  SongId => dSongId,
  Source => dSource,
}
import scala.util.matching.Regex

object ToData {

  final val DATE_PATTERN: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

  def process(elements: List[Either[ParserError, Element]]): List[Either[ParserError, dItem]] = {
    elements.map{
      _.flatMap {
        case a: Album =>
          process(a)
        case s: Song =>
          process(s)
        case e =>
          Left(ParserError(s"Unsupported in ToData: $e"))
      }
    }
  }

  def process(album: Album): Either[ParserError, dAlbum] = {
    val id = dAlbumId(album.id)
    for {
      releaseDate <- processDate(album.`release-date`, id)
    } yield {
      dAlbum(id, album.fullname, album.altname, album.designation, releaseDate)
    }
  }

  def process(credits: Credits): Either[ParserError, dCredits] = {
    for {
      source <- boxEitherOption(credits.source.map(process(_)))
    } yield {
      dCredits(credits.lyricist, credits.composer, source)
    }
  }

  def process(song: Song): Either[ParserError, dSong] = {
    val id = dSongId(song.id)
    for {
      releaseDate <- processDate(song.`release-date`, id)
      credits <- boxEitherOption(song.credits.map(process(_)))
    } yield {
      dSong(id, song.fullname, song.`fullname-en`, song.album.map(dAlbumId(_)), releaseDate, credits)
    }
  }

  def process(source: Source): Either[ParserError, dSource] = {
    Right(dSource(source.description))
  }

  def processDate(date: String, id: dId): Either[ParserError, dDate] = {
    date match {
      case DATE_PATTERN(year, month, day) =>
        Right(dDate(year.toInt, month.toInt, day.toInt))
      case _ =>
        Left(ParserError(id.s, s"cannot parse date in '$date'"))
    }
  }

  // -----

  private def boxEitherOption[A, B](in: Option[Either[A, B]]): Either[A, Option[B]] =
    in match {
      case Some(e) => e.map(Some(_))
      case None => Right(None)
    }

}