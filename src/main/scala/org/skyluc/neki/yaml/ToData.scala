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
  Lyrics => dLyrics,
  LyricsLanguage => dLyricsLanguage,
  LyricsLineEntry => dLyricsLineEntry,
  LyricsSection => dLyricsSection,
  LyricsStatus => dLyricsStatus,
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
  RefMediaIds => dRefMediaIds,
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
  YouTubeShort => dYouTubeShort,
  YouTubeShortId => dYouTubeShortId,
  YouTubeVideo => dYouTubeVideo,
  YouTubeVideoId => dYouTubeVideoId,
  Zaiko => dZaiko,
  ZaikoId => dZaikoId,
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
      case y: YouTubeShort =>
        process(y)
      case y: YouTubeVideo =>
        process(y)
      case z: Zaiko =>
        processZaiko(z)
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

  def process(credits: Credits): dCredits = {
    dCredits(credits.lyricist, credits.composer, credits.source.map(process))
  }

  def process(coverImage: CoverImage, id: dId[?]): Either[ParserError, dCoverImage] = {
    val candidates: List[Either[ParserError, dCoverImage]] = List(
      coverImage.file.map { file =>
        Right(dFileCoverImage(file.filename, process(file.source)))
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

  def processIds(refIds: List[Id], id: dId[?]): Either[ParserError, List[dId[?]]] = {
    throughList(refIds, id)(process)
  }

  def process(refId: Id, id: dId[?]): Either[ParserError, dId[?]] = {
    val candidates: List[dId[?]] = List(
      refId.song.map(dSongId(_)),
      refId.show.map(process),
    ).flatten

    // check only one defined
    candidates match {
      case head :: Nil =>
        Right(head)
      case Nil =>
        Left(ParserError(id, "No id reference specified"))
      case _ =>
        Left(ParserError(id, "Too many id references specified"))
    }
  }

  def process(lyrics: Lyrics, id: dId[?]): Either[ParserError, dLyrics] = {
    val status = dLyricsStatus(lyrics.status.code, lyrics.status.description)
    val languages = lyrics.languages.map(process)
    for {
      sections <- throughList(lyrics.sections) { section =>
        process(section, id)
      }
    } yield {
      dLyrics(status, languages, sections)
    }
  }

  def process(language: LyricsLanguage): dLyricsLanguage = {
    dLyricsLanguage(
      language.id,
      language.name,
      language.details,
      language.baseurl,
      language.urltext,
      language.active,
      language.fixed,
      language.notranslation,
      language.source.map(process(_)),
    )
  }

  def process(line: LyricsLine, id: dId[?]): Either[ParserError, Map[String, List[dLyricsLineEntry]]] = {
    val languages: List[(String, List[dLyricsLineEntry])] = List(
      line.ol.map { entries =>
        ("ol", entries.map(e => dLyricsLineEntry(Some(e), None)))
      },
      line.oll.map { entry =>
        ("ol", List(dLyricsLineEntry(Some(entry), None)))
      },
      line.tr.map { entries =>
        ("tr", entries.map(e => dLyricsLineEntry(Some(e), None)))
      },
      line.trl.map { entry =>
        ("tr", List(dLyricsLineEntry(Some(entry), None)))
      },
      line.ro.map { entries =>
        ("ro", entries.map(e => dLyricsLineEntry(Some(e), None)))
      },
      line.ww.map { entries =>
        // TODO: should not be both None
        ("ww", entries.map(e => dLyricsLineEntry(e.w, e.d)))
      },
      line.gg.map { entry =>
        ("gg", List(dLyricsLineEntry(Some(entry), None)))
      },
      line.en.map { entry =>
        ("en", List(dLyricsLineEntry(Some(entry), None)))
      },
    ).flatten
    if (languages.isEmpty) {
      Left(ParserError(id, s"At least one language has to defined in a lyrics line: $line"))
    } else {
      Right(languages.toMap)
    }
  }

  def process(section: LyricsSection, id: dId[?]): Either[ParserError, dLyricsSection] = {
    for {
      lines <- throughList(section.lines)(l => process(l, id))
    } yield {
      dLyricsSection(lines)
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
      video <- boxEitherOption(multimedia.video.map(processMultiMediaIds(_, id)))
      live <- boxEitherOption(multimedia.live.map(processMultiMediaIds(_, id)))
      concert <- boxEitherOption(multimedia.concert.map(processMultiMediaIds(_, id)))
      short <- boxEitherOption(multimedia.short.map(processMultiMediaIds(_, id)))
      additional <- boxEitherOption(multimedia.additional.map(processMultiMediaIds(_, id)))
    } yield {
      dMultiMediaBlock(
        video.getOrElse(Nil),
        live.getOrElse(Nil),
        concert.getOrElse(Nil),
        short.getOrElse(Nil),
        additional.getOrElse(Nil),
      )
    }
  }

  def processMultiMediaIds(multimediaIds: List[MultiMediaId], id: dId[?]): Either[ParserError, List[dMultiMediaId]] = {
    throughList(multimediaIds, id)(process)
  }

  def process(multimediaId: MultiMediaId, id: dId[?]): Either[ParserError, dMultiMediaId] = {
    val candidates: List[dMultiMediaId] = List(
      multimediaId.youtubevideo.map(dYouTubeVideoId(_)),
      multimediaId.youtubeshort.map(dYouTubeShortId(_)),
      multimediaId.zaiko.map(z => dZaikoId(z.channel, z.id)),
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
    Right(dNavigationItem(navigationItem.name, navigationItem.link, navigationItem.highlight))
  }

  def process(refMediaIds: RefMediaIds): dRefMediaIds = {
    dRefMediaIds(refMediaIds.account, refMediaIds.ids)
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
      dSite(navigation, band, site.youtubevideo.map(process), site.youtubeshort.map(process))
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
      coverImage <- process(song.`cover-image`, id)
      multimedia <- boxEitherOption(song.multimedia.map(process(_, id)))
      lyrics <- boxEitherOption(song.lyrics.map(process(_, id)))
    } yield {
      dSong(
        id,
        song.fullname,
        song.`fullname-en`,
        song.album.map(dAlbumId(_)),
        releaseDate,
        song.credits.map(process(_)),
        coverImage,
        multimedia.getOrElse(dMultiMediaBlock.EMPTY),
        lyrics,
      )
    }
  }

  def process(source: Source): dSource = {
    dSource(source.description)
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

  def process(youtubeshort: YouTubeShort): Either[ParserError, dYouTubeShort] = {
    val id = dYouTubeShortId(youtubeshort.id)
    for {
      publishedDate <- processDate(youtubeshort.`published-date`, id)
      relatedTo <- boxEitherOption(youtubeshort.`related-to`.map(processIds(_, id)))
    } yield {
      dYouTubeShort(id, youtubeshort.label, youtubeshort.info, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def process(youtubevideo: YouTubeVideo): Either[ParserError, dYouTubeVideo] = {
    val id = dYouTubeVideoId(youtubevideo.id)
    for {
      publishedDate <- processDate(youtubevideo.`published-date`, id)
      relatedTo <- boxEitherOption(youtubevideo.`related-to`.map(processIds(_, id)))
    } yield {
      dYouTubeVideo(id, youtubevideo.label, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def processZaiko(zaiko: Zaiko): Either[ParserError, dZaiko] = {
    val id = dZaikoId(zaiko.channel, zaiko.id)
    for {
      publishedDate <- processDate(zaiko.`published-date`, id)
      expirationDate <- boxEitherOption(zaiko.`expiration-date`.map(processDate(_, id)))
    } yield {
      dZaiko(
        id,
        zaiko.label,
        zaiko.`cover-image`,
        publishedDate,
        expirationDate,
      )
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
