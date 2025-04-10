package org.skyluc.neki.yaml

import org.skyluc.neki.{data => d}
import scala.util.matching.Regex
import scala.annotation.tailrec

object ToData {

  case class ToDataResult(
      main: Either[ParserError, d.Item[?]],
      related: List[ToDataResult],
  ) {
    def flatten(): List[Either[ParserError, d.Item[?]]] = {
      main :: related.flatMap(_.flatten())
    }
  }

  final val DATE_PATTERN: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

  def process(parserResults: List[ParserResult]): List[Either[ParserError, d.Item[?]]] = {
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

  def process(element: Either[ParserError, Element]): Either[ParserError, d.Item[?]] = {
    element.flatMap {
      case a: Album =>
        processAlbum(a)
      case c: ChronologyPage =>
        processChronologyPage(c)
      case m: MediaAudio =>
        processMediaAudio(m)
      case m: MediaWritten =>
        processMediaWritten(m)
      case m: MusicPage =>
        processMusicPage(m)
      case s: Show =>
        processShow(s)
      case s: ShowsPage =>
        processShowsPage(s)
      case s: Site =>
        processSite(s)
      case s: Song =>
        processSong(s)
      case t: Tour =>
        processTour(t)
      case y: YouTubeShort =>
        processYouTubeShort(y)
      case y: YouTubeVideo =>
        processYouTubeVideo(y)
      case z: Zaiko =>
        processZaiko(z)
      case e =>
        Left(ParserError(s"Unsupported in ToData: $e"))
    }
  }

  def processAlbum(album: Album): Either[ParserError, d.Album] = {
    val id = d.AlbumId(album.id)
    val songIds = album.songs.map(d.SongId(_))
    for {
      releaseDate <- processDate(album.`release-date`, id)
      coverImage <- processCoverImage(album.`cover-image`, id)
      multimedia <- boxEitherOption(album.multimedia.map(processMultiMedia(_, id)))
    } yield {
      d.Album(
        id,
        album.fullname,
        album.altname,
        album.designation,
        album.description,
        releaseDate,
        coverImage,
        songIds,
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
      )
    }
  }

  def processBand(band: Band): d.Band = {
    d.Band(
      processMembers(band.member),
      processSocialMedia(band.`social-media`),
    )
  }

  def processChronologyMarker(marker: ChronologyMarker, id: d.Id[?]): Either[ParserError, d.ChronologyMarker] = {

    val relatedMultimedia = boxEitherOption(marker.`related-multimedia`.map(processMultiMediaId(_, id)))
    val position = d.Position(marker.up, marker.in)

    val candidates: List[Either[ParserError, d.ChronologyMarker]] = List(
      marker.marker.map { label =>
        for {
          image <- marker.image.toRight(ParserError(id, s"No image specified for marker '$label'"))
          date <-
            marker.date
              .map(processDate(_, id))
              .getOrElse(Left(ParserError(id, s"No date specified for marker '$label'")))
        } yield {
          d.BaseMarker(label, date, image, position)
        }
      },
      marker.show.map { show =>
        val showId = processShowId(show)
        relatedMultimedia.map { rm =>
          d.ShowMarker(showId, marker.short, rm, position)
        }
      },
      marker.song.map { song =>
        relatedMultimedia.map { rm =>
          d.SongMarker(d.SongId(song), rm, position)
        }
      },
      marker.album.map { a =>
        val albumId = d.AlbumId(a)
        Right(d.AlbumMarker(albumId, position))
      },
      marker.youtubevideo.map { youtubevideo =>
        for {
          parentKey <- marker.`parent-key`.toRight(
            ParserError(id, s"No parent-key specified for youtubevideo marker '$youtubevideo'")
          )
        } yield {
          d.MultiMediaMarker(d.YouTubeVideoId(youtubevideo), parentKey, position)
        }
      },
      marker.media.map { media =>
        Right(d.MediaMarker(processMediaId(media), marker.short, position))
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

  def processChronologyPage(chronologyPage: ChronologyPage): Either[ParserError, d.ChronologyPage] = {
    val id = d.PageId(chronologyPage.id)
    for {
      startDate <- processDate(chronologyPage.`start-date`, id)
      endDate <- processDate(chronologyPage.`end-date`, id)
      markers <- throughList(chronologyPage.markers, id)(processChronologyMarker)
    } yield {
      d.ChronologyPage(
        id,
        d.Chronology(markers, startDate, endDate),
      )
    }
  }

  def processCredits(credits: Credits): d.Credits = {
    d.Credits(credits.lyricist, credits.composer, credits.source.map(processSource))
  }

  def processCoverImage(coverImage: CoverImage, id: d.Id[?]): Either[ParserError, d.CoverImage] = {
    val candidates: List[Either[ParserError, d.CoverImage]] = List(
      coverImage.file.map { file =>
        Right(d.FileCoverImage(file.filename, processSource(file.source)))
      },
      coverImage.album.map { album =>
        Right(d.AlbumCoverImage(d.AlbumId(album)))
      },
      coverImage.tour.map { tour =>
        Right(d.TourCoverImage(d.TourId(tour)))
      },
      coverImage.song.map { song =>
        Right(d.SongCoverImage(d.SongId(song)))
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

  def processIds(refIds: List[Id], id: d.Id[?]): Either[ParserError, List[d.Id[?]]] = {
    throughList(refIds, id)(processId)
  }

  def processId(refId: Id, id: d.Id[?]): Either[ParserError, d.Id[?]] = {
    val candidates: List[d.Id[?]] = List(
      refId.song.map(d.SongId(_)),
      refId.show.map(processShowId),
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

  def processLyrics(lyrics: Lyrics, id: d.Id[?]): Either[ParserError, d.Lyrics] = {
    val status = d.LyricsStatus(lyrics.status.code, lyrics.status.description)
    val languages = lyrics.languages.map(processLyricsLanguage)
    for {
      sections <- throughList(lyrics.sections) { section =>
        processLyricsSection(section, id)
      }
    } yield {
      d.Lyrics(status, languages, sections)
    }
  }

  def processLyricsLanguage(language: LyricsLanguage): d.LyricsLanguage = {
    d.LyricsLanguage(
      language.id,
      language.name,
      language.details,
      language.baseurl,
      language.urltext,
      language.active,
      language.fixed,
      language.notranslation,
      language.source.map(processSource),
    )
  }

  def processLyricsLine(line: LyricsLine, id: d.Id[?]): Either[ParserError, Map[String, List[d.LyricsLineEntry]]] = {
    val languages: List[(String, List[d.LyricsLineEntry])] = List(
      line.ol.map { entries =>
        ("ol", entries.map(e => d.LyricsLineEntry(Some(e), None)))
      },
      line.oll.map { entry =>
        ("ol", List(d.LyricsLineEntry(Some(entry), None)))
      },
      line.tr.map { entries =>
        ("tr", entries.map(e => d.LyricsLineEntry(Some(e), None)))
      },
      line.trl.map { entry =>
        ("tr", List(d.LyricsLineEntry(Some(entry), None)))
      },
      line.atr.map { entries =>
        ("atr", entries.map(e => d.LyricsLineEntry(Some(e), None)))
      },
      line.ro.map { entries =>
        ("ro", entries.map(e => d.LyricsLineEntry(Some(e), None)))
      },
      line.aro.map { entries =>
        ("aro", entries.map(e => d.LyricsLineEntry(Some(e), None)))
      },
      line.ww.map { entries =>
        // TODO: should not be both None
        ("ww", entries.map(e => d.LyricsLineEntry(e.w, e.d)))
      },
      line.gg.map { entry =>
        ("gg", List(d.LyricsLineEntry(Some(entry), None)))
      },
      line.en.map { entry =>
        ("en", List(d.LyricsLineEntry(Some(entry), None)))
      },
    ).flatten
    if (languages.isEmpty) {
      Left(ParserError(id, s"At least one language has to defined in a lyrics line: $line"))
    } else {
      Right(languages.toMap)
    }
  }

  def processLyricsSection(section: LyricsSection, id: d.Id[?]): Either[ParserError, d.LyricsSection] = {
    for {
      lines <- throughList(section.lines)(l => processLyricsLine(l, id))
    } yield {
      d.LyricsSection(lines)
    }
  }

  def processMediaAudio(media: MediaAudio): Either[ParserError, d.MediaAudio] = {
    val id = d.MediaId(media.year, media.id)
    for {
      publishedDate <- processDate(media.`published-date`, id)
      coverImage <- processCoverImage(media.`cover-image`, id)
    } yield {
      d.MediaAudio(
        id,
        media.radio,
        media.show,
        media.designation,
        media.program,
        media.host,
        media.member,
        media.webpage,
        publishedDate,
        media.description,
        coverImage,
      )
    }
  }

  def processMediaWritten(media: MediaWritten): Either[ParserError, d.MediaWritten] = {
    val id = d.MediaId(media.year, media.id)
    for {
      publishedDate <- processDate(media.`published-date`, id)
      coverImage <- processCoverImage(media.`cover-image`, id)
    } yield {
      d.MediaWritten(
        id,
        media.publication,
        media.issue,
        media.designation,
        media.journalist,
        media.member,
        media.`article-page`,
        media.webpage,
        publishedDate,
        media.description,
        coverImage,
      )
    }
  }

  def processMediaId(mediaId: MediaId): d.MediaId = {
    d.MediaId(mediaId.year, mediaId.id)
  }

  def processMember(member: Member): d.Member = {
    d.Member(
      member.id,
      member.name,
      member.role,
      processSocialMedia(member.`social-media`),
    )
  }

  def processMembers(member: Members): d.Members = {
    d.Members(
      processMember(member.cocoro),
      processMember(member.hika),
      processMember(member.kanade),
      processMember(member.natsu),
    )
  }

  def processMultiMedia(multimedia: MultiMedia, id: d.Id[?]): Either[ParserError, d.MultiMediaBlock] = {
    for {
      video <- boxEitherOption(multimedia.video.map(processMultiMediaIds(_, id)))
      live <- boxEitherOption(multimedia.live.map(processMultiMediaIds(_, id)))
      concert <- boxEitherOption(multimedia.concert.map(processMultiMediaIds(_, id)))
      short <- boxEitherOption(multimedia.short.map(processMultiMediaIds(_, id)))
      additional <- boxEitherOption(multimedia.additional.map(processMultiMediaIds(_, id)))
    } yield {
      d.MultiMediaBlock(
        video.getOrElse(Nil),
        live.getOrElse(Nil),
        concert.getOrElse(Nil),
        short.getOrElse(Nil),
        additional.getOrElse(Nil),
      )
    }
  }

  def processMultiMediaIds(
      multimediaIds: List[MultiMediaId],
      id: d.Id[?],
  ): Either[ParserError, List[d.MultiMediaId]] = {
    throughList(multimediaIds, id)(processMultiMediaId)
  }

  def processMultiMediaId(multimediaId: MultiMediaId, id: d.Id[?]): Either[ParserError, d.MultiMediaId] = {
    val candidates: List[d.MultiMediaId] = List(
      multimediaId.youtubevideo.map(d.YouTubeVideoId(_)),
      multimediaId.youtubeshort.map(d.YouTubeShortId(_)),
      multimediaId.zaiko.map(z => d.ZaikoId(z.channel, z.id)),
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

  def processMusicId(musicId: MusicId, id: d.Id[?]): Either[ParserError, d.AlbumId | d.SongId] = {
    val candidates: List[d.AlbumId | d.SongId] = List(
      musicId.album.map(d.AlbumId(_)),
      musicId.song.map(d.SongId(_)),
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

  def processMusicPage(musicPage: MusicPage): Either[ParserError, d.MusicPage] = {
    val id = d.PageId(musicPage.id)
    for {
      musicIds <- throughList(musicPage.music, id)(processMusicId)
    } yield {
      d.MusicPage(id, musicIds)
    }
  }

  def processNavigation(navigation: Navigation): Either[ParserError, d.Navigation] = {
    for {
      main <- throughList(navigation.main)(processNavigationItem)
      support <- throughList(navigation.support)(processNavigationItem)
    } yield {
      d.Navigation(main, support)
    }
  }

  def processNavigationItem(navigationItem: NavigationItem): Either[ParserError, d.NavigationItem] = {
    Right(d.NavigationItem(navigationItem.name, navigationItem.link, navigationItem.highlight))
  }

  def processNewsItem(newsItem: NewsItem): d.BandNews = {
    d.BandNews(newsItem.title, newsItem.content, newsItem.url)
  }

  def processMediaIds(refMediaIds: RefMediaIds): d.RefMediaIds = {
    d.RefMediaIds(refMediaIds.account, refMediaIds.ids)
  }

  def processShow(show: Show): Either[ParserError, d.Show] = {
    val id = d.ShowId(show.year, show.id)
    val tourId = show.tour.map(d.TourId(_))
    for {
      date <- processDate(show.date, id)
      coverImage <- processCoverImage(show.`cover-image`, id)
      multimedia <- boxEitherOption(show.multimedia.map(processMultiMedia(_, id)))
    } yield {
      d.Show(
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
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
      )
    }
  }

  def processShowId(showId: ShowId): d.ShowId = {
    d.ShowId(showId.year, showId.id)
  }

  def processShowOrTourId(showId: ShowOrTourId, id: d.Id[?]): Either[ParserError, d.ShowId | d.TourId] = {
    val candidates: List[d.ShowId | d.TourId] = List(
      showId.tour.map(d.TourId(_)),
      showId.show.map { s =>
        d.ShowId(s.year, s.id)
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

  def processShowsPage(showsPage: ShowsPage): Either[ParserError, d.ShowsPage] = {
    val id = d.PageId(showsPage.id)
    for {
      showsIds <- throughList(showsPage.shows, id)(processShowOrTourId)
    } yield {
      d.ShowsPage(id, showsIds)
    }
  }

  def processSite(site: Site): Either[ParserError, d.Site] = {
    val band = processBand(site.band)
    for {
      navigation <- processNavigation(site.navigation)
    } yield {
      d.Site(
        navigation,
        band,
        site.youtubevideo.map(processMediaIds),
        site.youtubeshort.map(processMediaIds),
        site.news.map(processNewsItem),
      )
    }
  }

  def processSocialMedia(socialMedia: SocialMedia): d.SocialMedia = {
    d.SocialMedia(
      socialMedia.instagram,
      socialMedia.tiktok,
      socialMedia.youtube,
      socialMedia.x,
    )
  }

  def processSong(song: Song): Either[ParserError, d.Song] = {
    val id = d.SongId(song.id, song.dark)
    for {
      releaseDate <- processDate(song.`release-date`, id)
      coverImage <- processCoverImage(song.`cover-image`, id)
      multimedia <- boxEitherOption(song.multimedia.map(processMultiMedia(_, id)))
      lyrics <- boxEitherOption(song.lyrics.map(processLyrics(_, id)))
    } yield {
      d.Song(
        id,
        song.fullname,
        song.`fullname-en`,
        song.album.map(d.AlbumId(_)),
        releaseDate,
        song.credits.map(processCredits(_)),
        coverImage,
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
        lyrics,
      )
    }
  }

  def processSource(source: Source): d.Source = {
    d.Source(source.description, source.url)
  }

  def processTour(tour: Tour): Either[ParserError, d.Tour] = {
    val id = d.TourId(tour.id)
    val shows = tour.shows.map(processShowId)
    for {
      firstDate <- processDate(tour.`first-date`, id)
      lastDate <- processDate(tour.`last-date`, id)
      coverImage <- processCoverImage(tour.`cover-image`, id)
    } yield {
      d.Tour(id, tour.fullname, tour.shortname, firstDate, lastDate, tour.`event-page`, coverImage, shows)
    }
  }

  def processYouTubeShort(youtubeshort: YouTubeShort): Either[ParserError, d.YouTubeShort] = {
    val id = d.YouTubeShortId(youtubeshort.id)
    for {
      publishedDate <- processDate(youtubeshort.`published-date`, id)
      relatedTo <- boxEitherOption(youtubeshort.`related-to`.map(processIds(_, id)))
    } yield {
      d.YouTubeShort(id, youtubeshort.label, youtubeshort.info, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def processYouTubeVideo(youtubevideo: YouTubeVideo): Either[ParserError, d.YouTubeVideo] = {
    val id = d.YouTubeVideoId(youtubevideo.id)
    for {
      publishedDate <- processDate(youtubevideo.`published-date`, id)
      relatedTo <- boxEitherOption(youtubevideo.`related-to`.map(processIds(_, id)))
    } yield {
      d.YouTubeVideo(id, youtubevideo.label, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def processZaiko(zaiko: Zaiko): Either[ParserError, d.Zaiko] = {
    val id = d.ZaikoId(zaiko.channel, zaiko.id)
    for {
      publishedDate <- processDate(zaiko.`published-date`, id)
      expirationDate <- boxEitherOption(zaiko.`expiration-date`.map(processDate(_, id)))
    } yield {
      d.Zaiko(
        id,
        zaiko.label,
        zaiko.`cover-image`,
        publishedDate,
        expirationDate,
      )
    }
  }

  def processDate(date: String, id: d.Id[?]): Either[ParserError, d.Date] = {
    date match {
      case DATE_PATTERN(year, month, day) =>
        Right(d.Date(year.toInt, month.toInt, day.toInt))
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

  private def throughList[A, B, C](in: List[A], id: d.Id[?])(f: (A, d.Id[?]) => Either[B, C]): Either[B, List[C]] = {
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
