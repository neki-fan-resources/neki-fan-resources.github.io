package org.skyluc.neki.yaml

import org.skyluc.neki.data.{
  Album => dAlbum,
  AlbumCoverImage => dAlbumCoverImage,
  AlbumId => dAlbumId,
  AlbumMarker => dAlbumMarker,
  Band => dBand,
  BaseMarker => dBaseMarker,
  Chronology => dChronology,
  ChronologyMarker => dChronologyMarker,
  ChronologyPage => dChronologyPage,
  CoverImage => dCoverImage,
  Credits => dCredits,
  Date => dDate,
  FileCoverImage => dFileCoverImage,
  Id => dId,
  Item => dItem,
  Media => dMedia,
  MediaId => dMediaId,
  MediaMarker => dMediaMarker,
  Member => dMember,
  Members => dMembers,
  MultiMediaId => dMultiMediaId,
  MultiMediaBlock => dMultiMediaBlock,
  MultiMediaMarker => dMultiMediaMarker,
  MusicPage => dMusicPage,
  Navigation => dNavigation,
  NavigationItem => dNavigationItem,
  PageId => dPageId,
  Position => dPosition,
  Show => dShow,
  ShowId => dShowId,
  ShowMarker => dShowMarker,
  ShowsPage => dShowsPage,
  Site => dSite,
  SocialMedia => dSocialMedia,
  Song => dSong,
  SongId => dSongId,
  SongMarker => dSongMarker,
  Source => dSource,
  Tour => dTour,
  TourCoverImage => dTourCoverImage,
  TourId => dTourId,
  YouTubeVideo => dYouTubeVideo,
  YouTubeVideoId => dYouTubeVideoId,
}
import scala.util.matching.Regex
import scala.annotation.tailrec

object ToData {

  case class ToDataResult(
      main: Either[ParserError, dItem[?]],
      related: List[ToDataResult],
  ) {
    def flatten(): List[Either[ParserError, dItem[?]]] = {
      main :: related.flatMap(_.flatten())
    }
  }

  final val DATE_PATTERN: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

  def process(parserResults: List[ParserResult]): List[Either[ParserError, dItem[?]]] = {
    parserResults.flatMap(pr => process(pr).flatten())
  }

  def process(parserResult: ParserResult): ToDataResult = {
    val mainElement = process(parserResult.main)
    val relatedElements = parserResult.related.map(process(_))

    val linkedRelatedElements = mainElement match {
      case Right(item) =>
        relatedElements.map { re =>
          re.copy(main = re.main.map(_.withRelatedToGen(item.id)))
        }
      case _ =>
        relatedElements
    }

    val mainElementWithRelated = mainElement.map { m =>
      relatedElements.foldLeft(m) { (main, result) =>
        result.main.toOption.map(item => main.withRelatedToGen(item.id)).getOrElse(main)
      }
    }

    ToDataResult(mainElementWithRelated, linkedRelatedElements)

  }

  def process(element: Either[ParserError, Element]): Either[ParserError, dItem[?]] = {
    element.flatMap {
      case a: Album =>
        process(a)
      case c: ChronologyPage =>
        process(c)
      case m: Media =>
        process(m)
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
      case y: YouTubeVideo =>
        process(y)
      case e =>
        Left(ParserError(s"Unsupported in ToData: $e"))
    }
  }

  def process(album: Album): Either[ParserError, dAlbum] = {
    val id = dAlbumId(album.id)
    val songIds = album.songs.map(dSongId(_))
    for {
      releaseDate <- processDate(album.`release-date`, id)
      coverImage <- process(album.`cover-image`, id)
      multimedia <- boxEitherOption(album.multimedia.map(process(_, id)))
    } yield {
      dAlbum(
        id,
        album.fullname,
        album.altname,
        album.designation,
        releaseDate,
        coverImage,
        songIds,
        multimedia.getOrElse(dMultiMediaBlock.EMPTY),
      )
    }
  }

  def process(band: Band): dBand = {
    dBand(
      process(band.member),
      process(band.`social-media`),
    )
  }

  def process(marker: ChronologyMarker, id: dId[?]): Either[ParserError, dChronologyMarker] = {

    val relatedMultimedia = boxEitherOption(marker.`related-multimedia`.map(process(_, id)))
    val position = dPosition(marker.up, marker.in)

    val candidates: List[Either[ParserError, dChronologyMarker]] = List(
      marker.marker.map { label =>
        for {
          image <- marker.image.toRight(ParserError(id, s"No image specified for marker '$label'"))
          date <-
            marker.date
              .map(processDate(_, id))
              .getOrElse(Left(ParserError(id, s"No date specified for marker '$label'")))
        } yield {
          dBaseMarker(label, date, image, position)
        }
      },
      marker.show.map { show =>
        val showId = process(show)
        relatedMultimedia.map { rm =>
          dShowMarker(showId, marker.short, rm, position)
        }
      },
      marker.song.map { song =>
        relatedMultimedia.map { rm =>
          dSongMarker(dSongId(song), rm, position)
        }
      },
      marker.album.map { a =>
        val albumId = dAlbumId(a)
        Right(dAlbumMarker(albumId, position))
      },
      marker.youtubevideo.map { youtubevideo =>
        for {
          parentKey <- marker.`parent-key`.toRight(
            ParserError(id, s"No parent-key specified for youtubevideo marker '$youtubevideo'")
          )
        } yield {
          dMultiMediaMarker(dYouTubeVideoId(youtubevideo), parentKey, position)
        }
      },
      marker.interview.map { interview =>
        Right(dMediaMarker(process(interview), marker.short, position))
      },
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        head
      case Nil =>
        Left(ParserError(id, s"No valid marker specified: $marker"))
      case _ =>
        Left(ParserError(id, s"Too many valid marker specified: $marker"))
    }
  }

  def process(chronologyPage: ChronologyPage): Either[ParserError, dChronologyPage] = {
    val id = dPageId(chronologyPage.id)
    for {
      startDate <- processDate(chronologyPage.`start-date`, id)
      endDate <- processDate(chronologyPage.`end-date`, id)
      markers <- throughList(chronologyPage.markers, id)(process)
    } yield {
      dChronologyPage(
        id,
        dChronology(markers, startDate, endDate),
      )
    }
  }

  def process(credits: Credits): Either[ParserError, dCredits] = {
    for {
      source <- boxEitherOption(credits.source.map(process))
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

  def process(media: Media): Either[ParserError, dMedia] = {
    val id = dMediaId(media.year, media.id)
    for {
      publishedDate <- processDate(media.`published-date`, id)
      coverImage <- process(media.`cover-image`, id)
    } yield {
      dMedia(
        id,
        media.radio,
        media.show,
        media.program,
        media.host,
        media.member,
        media.webpage,
        publishedDate,
        coverImage,
      )
    }

  }

  def process(mediaId: MediaId): dMediaId = {
    dMediaId(mediaId.year, mediaId.id)
  }

  def process(member: Member): dMember = {
    dMember(
      member.id,
      member.name,
      member.role,
      process(member.`social-media`),
    )
  }

  def process(member: Members): dMembers = {
    dMembers(
      process(member.cocoro),
      process(member.hika),
      process(member.kanade),
      process(member.natsu),
    )
  }

  def process(multimedia: MultiMedia, id: dId[?]): Either[ParserError, dMultiMediaBlock] = {
    for {
      video <- boxEitherOption(multimedia.video.map(process(_, id)))
      live <- boxEitherOption(multimedia.live.map(process(_, id)))
      additional <- boxEitherOption(multimedia.additional.map(process(_, id)))
    } yield {
      dMultiMediaBlock(video.getOrElse(Nil), live.getOrElse(Nil), additional.getOrElse(Nil))
    }
  }

  def process(multimediaIds: List[MultiMediaId], id: dId[?]): Either[ParserError, List[dMultiMediaId]] = {
    throughList(multimediaIds, id)(process)
  }

  def process(multimediaId: MultiMediaId, id: dId[?]): Either[ParserError, dMultiMediaId] = {
    val candidates: List[dMultiMediaId] = List(
      multimediaId.youtubevideo.map(dYouTubeVideoId(_))
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        Right(head)
      case Nil =>
        Left(ParserError(id, "No multimedia reference specified"))
      case _ =>
        Left(ParserError(id, "Too many multimedia references specified"))
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
    val id = dPageId(musicPage.id)
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
      multimedia <- boxEitherOption(show.multimedia.map(process(_, id)))
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
        multimedia.getOrElse(dMultiMediaBlock.EMPTY),
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
    val id = dPageId(showsPage.id)
    for {
      showsIds <- throughList(showsPage.shows, id)(process)
    } yield {
      dShowsPage(id, showsIds)
    }
  }

  def process(site: Site): Either[ParserError, dSite] = {
    val band = process(site.band)
    for {
      navigation <- process(site.navigation)
    } yield {
      dSite(navigation, band)
    }
  }

  def process(socialMedia: SocialMedia): dSocialMedia = {
    dSocialMedia(
      socialMedia.instagram,
      socialMedia.tiktok,
      socialMedia.youtube,
      socialMedia.x,
    )
  }

  def process(song: Song): Either[ParserError, dSong] = {
    val id = dSongId(song.id)
    for {
      releaseDate <- processDate(song.`release-date`, id)
      credits <- boxEitherOption(song.credits.map(process(_)))
      coverImage <- process(song.`cover-image`, id)
      multimedia <- boxEitherOption(song.multimedia.map(process(_, id)))
    } yield {
      dSong(
        id,
        song.fullname,
        song.`fullname-en`,
        song.album.map(dAlbumId(_)),
        releaseDate,
        credits,
        coverImage,
        multimedia.getOrElse(dMultiMediaBlock.EMPTY),
      )
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

  def process(youtubevideo: YouTubeVideo): Either[ParserError, dYouTubeVideo] = {
    val id = dYouTubeVideoId(youtubevideo.id)
    for {
      publishedDate <- processDate(youtubevideo.`published-date`, id)
    } yield {
      dYouTubeVideo(id, youtubevideo.label, publishedDate)
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
