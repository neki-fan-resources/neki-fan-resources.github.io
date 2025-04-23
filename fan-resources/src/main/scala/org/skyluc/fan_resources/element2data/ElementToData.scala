package org.skyluc.fan_resources.element2data

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.element2data.DataTransformer.ToDataError
import org.skyluc.fan_resources.yaml.*

import scala.annotation.tailrec

class ElementToData extends Processor[ToDataError, d.Datum[?]] {

  override def processAlbum(album: Album): Either[ToDataError, d.Datum[?]] =
    toAlbum(album)

  override def processLocalImage(localImage: LocalImage): Either[ToDataError, d.Datum[?]] =
    toLocalImage(localImage)

  override def processMediaAudio(mediaAudio: MediaAudio): Either[ToDataError, d.Datum[?]] =
    toMediaAudio(mediaAudio)

  override def processMediaWritten(mediaWritten: MediaWritten): Either[ToDataError, d.Datum[?]] =
    toMediaWritten(mediaWritten)

  override def processPostX(postX: PostX): Either[ToDataError, d.Datum[?]] =
    toPostX(postX)

  override def processShow(show: Show): Either[ToDataError, d.Datum[?]] =
    toShow(show)

  override def processSong(song: Song): Either[ToDataError, d.Datum[?]] =
    toSong(song)

  override def processTour(tour: Tour): Either[ToDataError, d.Datum[?]] =
    toTour(tour)

  override def processYouTubeShort(youtubeShort: YouTubeShort): Either[ToDataError, d.Datum[?]] =
    toYouTubeShort(youtubeShort)

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Either[ToDataError, d.Datum[?]] =
    toYouTubeVideo(youtubeVideo)

  override def processZaiko(zaiko: Zaiko): Either[ToDataError, d.Datum[?]] =
    toZaiko(zaiko)

  def toAlbum(album: Album): Either[ToDataError, d.Album] = {
    val id = d.AlbumId(album.id)
    val songIds = album.songs.map(d.SongId(_))
    for {
      releaseDate <- toDate(album.`release-date`, id)
      coverImage <- toCoverImage(album.`cover-image`, id)
      multimedia <- unboxBoxEitherOption(album.multimedia.map(toMultiMedia(_, id)))
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

  // TODO: replace with LocalImage
  def toCoverImage(coverImage: CoverImage, id: d.Id[?]): Either[ToDataError, d.CoverImage] = {
    val candidates: List[Either[ToDataError, d.CoverImage]] = List(
      coverImage.file.map { file =>
        Right(d.FileCoverImage(file.filename, toSource(file.source)))
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

    checkOnlyOneCandidatesEither(
      candidates,
      "cover image",
      "cover image",
      "",
      id,
    )
  }

  def toCredits(credits: Credits): d.Credits = {
    d.Credits(credits.lyricist, credits.composer, credits.source.map(toSource))
  }

  def toChronologyMarker(marker: ChronologyMarker, id: d.Id[?]): Either[ToDataError, d.ChronologyMarker] = {

    val relatedMultimedia = unboxBoxEitherOption(marker.`related-multimedia`.map(toMultiMediaId(_, id)))
    val position = d.Position(marker.up, marker.in)

    val candidates: List[Either[ToDataError, d.ChronologyMarker]] = List(
      marker.marker.map { label =>
        for {
          image <- marker.image.toRight(ToDataError(id, s"No image specified for marker '$label'"))
          date <-
            marker.date
              .map(toDate(_, id))
              .getOrElse(Left(ToDataError(id, s"No date specified for marker '$label'")))
          rMultimedia <- relatedMultimedia
        } yield {
          d.BaseMarker(d.BaseMarkerId.cleaned(label), label, date, image, rMultimedia, position)
        }
      },
      marker.show.map { show =>
        val showId = toShowId(show)
        relatedMultimedia.map { rm =>
          d.ShowMarker(d.ShowMarkerId(showId), marker.short, rm, position)
        }
      },
      marker.song.map { song =>
        relatedMultimedia.map { rm =>
          d.SongMarker(d.SongMarkerId(d.SongId(song)), rm, position)
        }
      },
      marker.album.map { a =>
        val albumId = d.AlbumId(a)
        Right(d.AlbumMarker(d.AlbumMarkerId(albumId), position))
      },
      marker.youtubevideo.map { youtubevideo =>
        for {
          parentKey <- marker.`parent-key`.toRight(
            ToDataError(id, s"No parent-key specified for youtubevideo marker '$youtubevideo'")
          )
        } yield {
          d.MultiMediaMarker(d.MultiMediaMarkerId(d.YouTubeVideoId(youtubevideo)), parentKey, position)
        }
      },
      marker.media.map { media =>
        Right(d.MediaMarker(d.MediaMarkerId(toMediaId(media)), marker.short, position))
      },
    ).flatten

    checkOnlyOneCandidatesEither(
      candidates,
      "marker",
      "chronology marker",
      marker.toString(),
      id,
    )
  }

  def toDate(date: String, id: d.Id[?]): Either[ToDataError, d.Date] = {
    date match {
      case d.Date.DATE_PATTERN(year, month, day) =>
        Right(d.Date(year.toInt, month.toInt, day.toInt))
      case _ =>
        Left(ToDataError(id, s"cannot parse date in '$date'"))
    }
  }

  def toId(refId: Id, id: d.Id[?]): Either[ToDataError, d.Id[?]] = {
    val candidates: List[d.Id[?]] = List(
      refId.song.map(d.SongId(_)),
      refId.show.map(toShowId),
    ).flatten

    checkOnlyOneCandidates(
      candidates,
      "id",
      "id",
      "",
      id,
    )(identity)
  }

  def toIds(refIds: List[Id], id: d.Id[?]): Either[ToDataError, List[d.Id[?]]] = {
    throughList(refIds, id)(toId)
  }

  def toLocalImage(localImage: LocalImage): Either[ToDataError, d.LocalImage] = {
    for {
      id <- toLocalImageId(localImage, null)
      publishedDate <- toDate(localImage.`published-date`, id)
    } yield {
      d.LocalImage(id, localImage.filename, localImage.label, publishedDate)
    }
  }

  def toLocalImageId(localImageId: WithLocalImageId, id: d.Id[?]): Either[ToDataError, d.LocalImageId] = {
    val candidates: List[d.ItemId[?]] = List(
      localImageId.media.map(toMediaId)
    ).flatten

    checkOnlyOneCandidates(
      candidates,
      "item id",
      "image",
      localImageId.id,
      id,
    )(itemId => d.LocalImageId(itemId, localImageId.id))
  }

  def toLyrics(lyrics: Lyrics, id: d.Id[?]): Either[ToDataError, d.Lyrics] = {
    val status = d.CriptionLationStatus(lyrics.status.code, lyrics.status.description)
    val languages = lyrics.languages.map(toLyricsLanguage)
    for {
      sections <- throughList(lyrics.sections) { section =>
        toLyricsSection(section, id)
      }
    } yield {
      d.Lyrics(status, languages, sections)
    }
  }

  def toLyricsLanguage(language: LyricsLanguage): d.LyricsLanguage = {
    d.LyricsLanguage(
      language.id,
      language.name,
      language.details,
      language.baseurl,
      language.urltext,
      language.active,
      language.fixed,
      language.notranslation,
      language.source.map(toSource),
    )
  }

  def toLyricsLine(line: LyricsLine, id: d.Id[?]): Either[ToDataError, Map[String, List[d.LyricsLineEntry]]] = {
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
      Left(ToDataError(id, s"At least one language has to defined in a lyrics line: $line"))
    } else {
      Right(languages.toMap)
    }
  }

  def toLyricsSection(section: LyricsSection, id: d.Id[?]): Either[ToDataError, d.LyricsSection] = {
    for {
      lines <- throughList(section.lines)(l => toLyricsLine(l, id))
    } yield {
      d.LyricsSection(lines)
    }
  }

  def toMediaAudio(media: MediaAudio): Either[ToDataError, d.MediaAudio] = {
    val id = d.MediaId(media.year, media.id)
    for {
      publishedDate <- toDate(media.`published-date`, id)
      coverImage <- toCoverImage(media.`cover-image`, id)
      multimedia <- unboxBoxEitherOption(media.multimedia.map(toMultiMedia(_, id)))
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
        media.summary.map(toSummary),
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
      )
    }
  }

  def toMediaId(mediaId: MediaId): d.MediaId = {
    d.MediaId(mediaId.year, mediaId.id)
  }

  def toMediaIds(refMediaIds: RefMediaIds): d.RefMediaIds = {
    d.RefMediaIds(refMediaIds.account, refMediaIds.ids)
  }

  def toMediaWritten(media: MediaWritten): Either[ToDataError, d.MediaWritten] = {
    val id = d.MediaId(media.year, media.id)
    for {
      publishedDate <- toDate(media.`published-date`, id)
      coverImage <- toCoverImage(media.`cover-image`, id)
      multimedia <- unboxBoxEitherOption(media.multimedia.map(toMultiMedia(_, id)))
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
        media.summary.map(toSummary),
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
      )
    }
  }

  def toMultiMedia(multimedia: MultiMedia, id: d.Id[?]): Either[ToDataError, d.MultiMediaBlock] = {
    for {
      video <- unboxBoxEitherOption(multimedia.video.map(toMultiMediaIds(_, id)))
      live <- unboxBoxEitherOption(multimedia.live.map(toMultiMediaIds(_, id)))
      concert <- unboxBoxEitherOption(multimedia.concert.map(toMultiMediaIds(_, id)))
      short <- unboxBoxEitherOption(multimedia.short.map(toMultiMediaIds(_, id)))
      image <- unboxBoxEitherOption(multimedia.image.map(toMultiMediaIds(_, id)))
      additional <- unboxBoxEitherOption(multimedia.additional.map(toMultiMediaIds(_, id)))
    } yield {
      d.MultiMediaBlock(
        video.getOrElse(Nil),
        live.getOrElse(Nil),
        concert.getOrElse(Nil),
        short.getOrElse(Nil),
        image.getOrElse(Nil),
        additional.getOrElse(Nil),
      )
    }
  }

  def toMultiMediaId(multimediaId: MultiMediaId, id: d.Id[?]): Either[ToDataError, d.MultiMediaId] = {
    unboxBoxEitherOption(multimediaId.localImage.map(toLocalImageId(_, id))).flatMap { localImageId =>
      val candidates: List[d.MultiMediaId] = List(
        localImageId,
        multimediaId.postXImage.map(toPostXImageId),
        multimediaId.youtubevideo.map(d.YouTubeVideoId(_)),
        multimediaId.youtubeshort.map(d.YouTubeShortId(_)),
        multimediaId.zaiko.map(z => d.ZaikoId(z.channel, z.id)),
      ).flatten

      // check only one defined
      candidates match {
        case head :: Nil =>
          Right(head)
        case Nil =>
          Left(ToDataError(id, "No multimedia reference specified"))
        case _ =>
          Left(ToDataError(id, "Too many multimedia references specified"))
      }
    }

  }

  def toMultiMediaIds(
      multimediaIds: List[MultiMediaId],
      id: d.Id[?],
  ): Either[ToDataError, List[d.MultiMediaId]] = {
    throughList(multimediaIds, id)(toMultiMediaId)
  }

  def toPostX(postX: PostX): Either[ToDataError, d.PostX] = {
    val id = d.PostXId(postX.id)
    for {
      publishedDate <- toDate(postX.`published-date`, id)
    } yield {
      d.PostX(
        id,
        postX.account,
        publishedDate,
        postX.info,
        postX.text,
        postX.image.map(_.map(toPostXImage(_, id, publishedDate))).getOrElse(Nil),
      )
    }
  }

  def processMusicId(musicId: MusicId, id: d.Id[?]): Either[ToDataError, d.AlbumId | d.SongId] = {
    val candidates: List[d.AlbumId | d.SongId] = List(
      musicId.album.map(d.AlbumId(_)),
      musicId.song.map(d.SongId(_)),
    ).flatten

    checkOnlyOneCandidates(
      candidates,
      "music id",
      "music id",
      "",
      id,
    )(identity)
  }

  def toPostXImage(postXImage: PostXImage, postXId: d.PostXId, publishedDate: d.Date): d.PostXImage = {
    d.PostXImage(d.PostXImageId(postXId.id, postXImage.id), postXImage.label, publishedDate, postXImage.info)
  }

  def toPostXImageId(postXImageId: PostXImageId): d.PostXImageId = {
    d.PostXImageId(postXImageId.postId, postXImageId.imageId)
  }

  def toShow(show: Show): Either[ToDataError, d.Show] = {
    val id = d.ShowId(show.year, show.id)
    val tourId = show.tour.map(d.TourId(_))
    for {
      date <- toDate(show.date, id)
      coverImage <- toCoverImage(show.`cover-image`, id)
      multimedia <- unboxBoxEitherOption(show.multimedia.map(toMultiMedia(_, id)))
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

  def toShowId(showId: ShowId): d.ShowId = {
    d.ShowId(showId.year, showId.id)
  }

  def toShowOrTourId(showId: ShowOrTourId, id: d.Id[?]): Either[ToDataError, d.ShowId | d.TourId] = {
    val candidates: List[d.ShowId | d.TourId] = List(
      showId.tour.map(d.TourId(_)),
      showId.show.map { s =>
        d.ShowId(s.year, s.id)
      },
    ).flatten

    checkOnlyOneCandidates(
      candidates,
      "show or tour",
      "show or tour id",
      showId.toString(),
      id,
    )(identity)
  }

  def toSong(song: Song): Either[ToDataError, d.Song] = {
    val id = d.SongId(song.id, song.dark)
    for {
      releaseDate <- toDate(song.`release-date`, id)
      coverImage <- toCoverImage(song.`cover-image`, id)
      multimedia <- unboxBoxEitherOption(song.multimedia.map(toMultiMedia(_, id)))
      lyrics <- unboxBoxEitherOption(song.lyrics.map(toLyrics(_, id)))
    } yield {
      d.Song(
        id,
        song.fullname,
        song.`fullname-en`,
        song.album.map(d.AlbumId(_)),
        releaseDate,
        song.description,
        song.credits.map(toCredits(_)),
        coverImage,
        multimedia.getOrElse(d.MultiMediaBlock.EMPTY),
        lyrics,
      )
    }
  }

  def toSource(source: Source): d.Source = {
    d.Source(source.description, source.url)
  }

  def toSummary(summary: Summary): d.Summary = {
    d.Summary(
      d.CriptionLationStatus(summary.status.code, summary.status.description),
      toSummaryItems(summary.items),
    )
  }

  def toSummaryItem(summaryItem: SummaryItem): d.SummaryItem = {
    d.SummaryItem(
      summaryItem.label,
      summaryItem.sub.map(toSummaryItems).getOrElse(Nil),
    )
  }

  def toSummaryItems(summaryItems: List[SummaryItem]): List[d.SummaryItem] = {
    summaryItems.map(toSummaryItem)
  }

  def toTour(tour: Tour): Either[ToDataError, d.Tour] = {
    val id = d.TourId(tour.id)
    val shows = tour.shows.map(toShowId)
    for {
      firstDate <- toDate(tour.`first-date`, id)
      lastDate <- toDate(tour.`last-date`, id)
      coverImage <- toCoverImage(tour.`cover-image`, id)
    } yield {
      d.Tour(id, tour.fullname, tour.shortname, firstDate, lastDate, tour.`event-page`, coverImage, shows)
    }
  }

  def toYouTubeShort(youtubeshort: YouTubeShort): Either[ToDataError, d.YouTubeShort] = {
    val id = d.YouTubeShortId(youtubeshort.id)
    for {
      publishedDate <- toDate(youtubeshort.`published-date`, id)
      relatedTo <- unboxBoxEitherOption(youtubeshort.`related-to`.map(toIds(_, id)))
    } yield {
      d.YouTubeShort(id, youtubeshort.label, youtubeshort.info, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def toYouTubeVideo(youtubevideo: YouTubeVideo): Either[ToDataError, d.YouTubeVideo] = {
    val id = d.YouTubeVideoId(youtubevideo.id)
    for {
      publishedDate <- toDate(youtubevideo.`published-date`, id)
      relatedTo <- unboxBoxEitherOption(youtubevideo.`related-to`.map(toIds(_, id)))
    } yield {
      d.YouTubeVideo(id, youtubevideo.label, publishedDate, relatedTo.getOrElse(Nil))
    }
  }

  def toZaiko(zaiko: Zaiko): Either[ToDataError, d.Zaiko] = {
    val id = d.ZaikoId(zaiko.channel, zaiko.id)
    for {
      publishedDate <- toDate(zaiko.`published-date`, id)
      expirationDate <- unboxBoxEitherOption(zaiko.`expiration-date`.map(toDate(_, id)))
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

  // ===================
  // common functions
  // ===================

  protected def checkOnlyOneCandidatesEither[A](
      candidates: List[Either[ToDataError, A]],
      ofWhat: String,
      forWhat: String,
      errorPointer: String,
      id: d.Id[?],
  ): Either[ToDataError, A] = {
    candidates match {
      case head :: Nil =>
        head
      case Nil =>
        Left(ToDataError(id, s"No $ofWhat reference specified for $forWhat '$errorPointer'"))
      case _ =>
        Left(ToDataError(id, s"Too many $ofWhat references specified for $forWhat '$errorPointer'"))
    }
  }

  protected def checkOnlyOneCandidates[A, B](
      candidates: List[A],
      ofWhat: String,
      forWhat: String,
      errorPointer: String,
      id: d.Id[?],
  )(builder: (A) => B): Either[ToDataError, B] = {
    candidates match {
      case head :: Nil =>
        Right(builder(head))
      case Nil =>
        Left(ToDataError(id, s"No $ofWhat reference specified for $forWhat '$errorPointer'"))
      case _ =>
        Left(ToDataError(id, s"Too many $ofWhat references specified for $forWhat '$errorPointer'"))
    }
  }

  protected def throughList[A, B, C](in: List[A])(f: (A) => Either[B, C]): Either[B, List[C]] = {
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

  protected def throughList[A, B, C](in: List[A], id: d.Id[?])(f: (A, d.Id[?]) => Either[B, C]): Either[B, List[C]] = {
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

  protected def unboxBoxEitherOption[A, B](in: Option[Either[A, B]]): Either[A, Option[B]] =
    in match {
      case Some(e) => e.map(Some(_))
      case None    => Right(None)
    }

}
