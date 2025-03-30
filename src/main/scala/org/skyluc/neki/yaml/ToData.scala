package org.skyluc.neki.yaml

import org.skyluc.neki.data.{
  Album => dAlbum,
  AlbumId => dAlbumId,
  CoverImage => dCoverImage,
  Credits => dCredits,
  Date => dDate,
  FileCoverImage => dFileCoverImage,
  Id => dId,
  Item => dItem,
  Navigation => dNavigation,
  NavigationItem => dNavigationItem,
  Site => dSite,
  Song => dSong,
  SongId => dSongId,
  Source => dSource,
}
import scala.util.matching.Regex
import scala.annotation.tailrec

object ToData {

  final val DATE_PATTERN: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

  def process(elements: List[Either[ParserError, Element]]): List[Either[ParserError, dItem[?]]] = {
    elements.map {
      _.flatMap {
        case a: Album =>
          process(a)
        case s: Site =>
          process(s)
        case s: Song =>
          process(s)
        case e =>
          Left(ParserError(s"Unsupported in ToData: $e"))
      }
    }
  }

  def process(album: Album): Either[ParserError, dAlbum] = {
    val id = dAlbumId(album.id)
    val songIds = album.songs.map(dSongId(_))
    for {
      releaseDate <- processDate(album.`release-date`, id)
      coverImage <- process(album.`cover-image`, id)
    } yield {
      dAlbum(id, album.fullname, album.altname, album.designation, releaseDate, coverImage, songIds)
    }
  }

  def process(credits: Credits): Either[ParserError, dCredits] = {
    for {
      source <- boxEitherOption(credits.source.map(process(_)))
    } yield {
      dCredits(credits.lyricist, credits.composer, source)
    }
  }

  def process(coverImage: CoverImage, id: dId): Either[ParserError, dCoverImage] = {
    val data: List[Either[ParserError, dFileCoverImage]] = List(
      coverImage.file.map { file =>
        for {
          source <- process(file.source)
        } yield {
          dFileCoverImage(file.filename, source)
        }
      }
    ).flatten

    // check only one defined
    data match {
      case head :: Nil =>
        head
      case Nil =>
        Left(ParserError(id, "No cover image reference specified"))
      case _ =>
        Left(ParserError(id, "Too many cover image reference specified"))
    }
  }

  def process(navigation: Navigation): Either[ParserError, dNavigation] = {
    for {
      main <- throughList(navigation.main)(process)
      support <- throughList(navigation.support)(process)
    } yield {
      dNavigation(main, support)
    }
  }

  def process(navigationItem: NavigationItem): Either[ParserError, dNavigationItem] = {
    Right(dNavigationItem(navigationItem.name, navigationItem.link))
  }

  def process(site: Site): Either[ParserError, dSite] = {
    for {
      navigation <- process(site.navigation)
    } yield {
      dSite(navigation)
    }
  }

  def process(song: Song): Either[ParserError, dSong] = {
    val id = dSongId(song.id)
    for {
      releaseDate <- processDate(song.`release-date`, id)
      credits <- boxEitherOption(song.credits.map(process(_)))
      coverImage <- process(song.`cover-image`, id)
    } yield {
      dSong(id, song.fullname, song.`fullname-en`, song.album.map(dAlbumId(_)), releaseDate, credits, coverImage)
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
        Left(ParserError(id, s"cannot parse date in '$date'"))
    }
  }

  // -----

  private def boxEitherOption[A, B](in: Option[Either[A, B]]): Either[A, Option[B]] =
    in match {
      case Some(e) => e.map(Some(_))
      case None    => Right(None)
    }

  private def throughList[A, B, C](in: List[A])(f: (A) => Either[B, C]): Either[B, List[C]] = {
    @tailrec def loop(in: List[A], acc: List[C]): Either[B, List[C]] = {
      in match {
        case head :: tail =>
          f(head) match {
            case Left(value) =>
              Left(value)
            case Right(value) =>
              loop(tail, value :: acc)
          }
        case Nil => Right(acc.reverse)
      }
    }
    loop(in, Nil)
  }

}
