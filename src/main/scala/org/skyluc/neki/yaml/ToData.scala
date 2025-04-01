package org.skyluc.neki.yaml

import org.skyluc.neki.data.{
  Album => dAlbum,
  AlbumCoverImage => dAlbumCoverImage,
  AlbumId => dAlbumId,
  CoverImage => dCoverImage,
  Credits => dCredits,
  Date => dDate,
  FileCoverImage => dFileCoverImage,
  Id => dId,
  Item => dItem,
  MusicPage => dMusicPage,
  Navigation => dNavigation,
  NavigationItem => dNavigationItem,
  PageId => dPageId,
  Show => dShow,
  ShowId => dShowId,
  ShowsPage => dShowsPage,
  Site => dSite,
  Song => dSong,
  SongId => dSongId,
  Source => dSource,
  Tour => dTour,
  TourCoverImage => dTourCoverImage,
  TourId => dTourId,
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
        case m: MusicPage =>
          process(m)
        case s: Show =>
          process(s)
        case s: ShowsPage =>
          process(s)
        case s: Site =>
          process(s)
        case s: Song =>
          process(s)
        case t: Tour =>
          process(t)
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

  def process(coverImage: CoverImage, id: dId[?]): Either[ParserError, dCoverImage] = {
    val candidates: List[Either[ParserError, dCoverImage]] = List(
      coverImage.file.map { file =>
        for {
          source <- process(file.source)
        } yield {
          dFileCoverImage(file.filename, source)
        }
      },
      coverImage.album.map { album =>
        Right(dAlbumCoverImage(dAlbumId(album)))
      },
      coverImage.tour.map { tour =>
        Right(dTourCoverImage(dTourId(tour)))
      },
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        head
      case Nil =>
        Left(ParserError(id, "No cover image reference specified"))
      case _ =>
        Left(ParserError(id, "Too many cover image references specified"))
    }
  }

  def processMusicId(musicId: MusicId, id: dId[?]): Either[ParserError, dAlbumId | dSongId] = {
    val candidates: List[dAlbumId | dSongId] = List(
      musicId.album.map(dAlbumId(_)),
      musicId.song.map(dSongId(_)),
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        Right(head)
      case Nil =>
        Left(ParserError(id, "No music reference specified"))
      case _ =>
        Left(ParserError(id, "Too many music references specified"))
    }
  }

  def process(musicPage: MusicPage): Either[ParserError, dMusicPage] = {
    val id = dPageId[dMusicPage](musicPage.id)
    for {
      musicIds <- throughList(musicPage.music, id)(processMusicId)
    } yield {
      dMusicPage(id, musicIds)
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

  def process(show: Show): Either[ParserError, dShow] = {
    val id = dShowId(show.year, show.id)
    val tourId = show.tour.map(dTourId(_))
    for {
      date <- processDate(show.date, id)
      coverImage <- process(show.`cover-image`, id)
    } yield {
      dShow(
        id,
        show.fullname,
        show.shortname,
        show.sublabel,
        date,
        tourId,
        show.location,
        show.`event-page`,
        show.setlistfm,
        coverImage,
      )
    }
  }

  def process(showId: ShowId): dShowId = {
    dShowId(showId.year, showId.id)
  }

  def process(showId: ShowOrTourId, id: dId[?]): Either[ParserError, dShowId | dTourId] = {
    val candidates: List[dShowId | dTourId] = List(
      showId.tour.map(dTourId(_)),
      showId.show.map { s =>
        dShowId(s.year, s.id)
      },
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        Right(head)
      case Nil =>
        Left(ParserError(id, "No show or tour reference specified"))
      case _ =>
        Left(ParserError(id, "Too many show or tour references specified"))
    }
  }

  def process(showsPage: ShowsPage): Either[ParserError, dShowsPage] = {
    val id = dPageId[dShowsPage](showsPage.id)
    for {
      showsIds <- throughList(showsPage.shows, id)(process)
    } yield {
      dShowsPage(id, showsIds)
    }
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

  def process(tour: Tour): Either[ParserError, dTour] = {
    val id = dTourId(tour.id)
    val shows = tour.shows.map(process)
    for {
      firstDate <- processDate(tour.`first-date`, id)
      lastDate <- processDate(tour.`last-date`, id)
      coverImage <- process(tour.`cover-image`, id)
    } yield {
      dTour(id, tour.fullname, tour.shortname, firstDate, lastDate, tour.`event-page`, coverImage, shows)
    }
  }

  def processDate(date: String, id: dId[?]): Either[ParserError, dDate] = {
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

  private def throughList[A, B, C](in: List[A], id: dId[?])(f: (A, dId[?]) => Either[B, C]): Either[B, List[C]] = {
    @tailrec def loop(in: List[A], acc: List[C]): Either[B, List[C]] = {
      in match {
        case head :: tail =>
          f(head, id) match {
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
