package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor => _, _}
import org.skyluc.neki_site.data._
import org.skyluc.fan_resources.html.ElementCompiledData
import scala.collection.mutable.HashMap
import org.skyluc.neki_site.html.pages.SongPage
import org.skyluc.fan_resources.html.ElementInfo
import org.skyluc.fan_resources.Common
import org.skyluc.neki_site.html.pages.ShowPage
import org.skyluc.fan_resources.html.Url
import org.skyluc.neki_site.html.pages.TourPage
import org.skyluc.neki_site.html.pages.MediaPage

class ElementCompiledDataGenerator(compilers: Compilers) extends Processor[ElementCompiledData] {

  override def processAlbumMarker(albumMarker: AlbumMarker): ElementCompiledData = ???

  override def processBaseMarker(baseMarker: BaseMarker): ElementCompiledData = ???

  override def processMediaMarker(mediaMarker: MediaMarker): ElementCompiledData = ???

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): ElementCompiledData = ???

  override def processShowMarker(showMarker: ShowMarker): ElementCompiledData = ???

  override def processSongMarker(songMarker: SongMarker): ElementCompiledData = ???

  val cache = HashMap[Id[?], ElementCompiledData]()

  def get(id: Id[?]): ElementCompiledData = {
    cache.getOrElseUpdate(
      id,
      compilers.data.all(id).process(this),
    )
  }

  def get(element: Datum[?]): ElementCompiledData = {
    cache.getOrElseUpdate(
      element.id,
      element.process(this),
    )
  }

  override def processAlbum(album: Album): ElementCompiledData = {
    val info =
      List(
        ElementInfo.dateDepending(
          Common.LABEL_EXPECTED,
          Common.LABEL_RELEASED,
          album.releaseDate,
          album.releaseDate.toString(),
          ElementInfo.INFO_LEVEL_MINIMUM,
        )
      )

    ElementCompiledData(
      album.designation,
      album.fullname,
      None,
      None,
      None,
      album.description,
      album.releaseDate,
      None,
      CoverImage.resolve(album.coverImage, album.designation, album.fullname, album, compilers),
      info,
      UrlResolver.resolve(album),
      Album.FROM_KEY,
    )

  }

  override def processLocalImage(localImage: LocalImage): ElementCompiledData = ???

  override def processMediaAudio(mediaAudio: MediaAudio): ElementCompiledData = {
    val info = List(
      Some(ElementInfo(MediaPage.LABEL_HOST, mediaAudio.host, ElementInfo.INFO_LEVEL_BASIC)),
      Some(
        ElementInfo.dateDepending(
          Common.LABEL_UPCOMING,
          Common.LABEL_PUBLISHED,
          mediaAudio.publishedDate,
          mediaAudio.publishedDate.toString(),
          ElementInfo.INFO_LEVEL_MINIMUM,
        )
      ),
      mediaAudio.webpage.map { w =>
        ElementInfo(None, MediaPage.VALUE_SHOW_PAGE, Some(Url(w)), ElementInfo.INFO_LEVEL_ALL)
      },
      mediaAudio.program.map { p =>
        ElementInfo(None, MediaPage.VALUE_PROGRAM, Some(Url(p)), ElementInfo.INFO_LEVEL_ALL)
      },
    ).flatten

    ElementCompiledData(
      mediaAudio.designation,
      mediaAudio.title(),
      None,
      Some(mediaAudio.member.mkString(", ")),
      None,
      mediaAudio.description,
      mediaAudio.publishedDate,
      Some(mediaAudio.publishedDate.toString()),
      CoverImage.resolve(mediaAudio.coverImage, mediaAudio.designation, mediaAudio.title(), mediaAudio, compilers),
      info,
      UrlResolver.resolve(mediaAudio),
      Media.FROM_KEY,
    )

  }

  override def processMediaWritten(mediaWritten: MediaWritten): ElementCompiledData = {
    val info = List(
      Some(
        ElementInfo.dateDepending(
          Common.LABEL_UPCOMING,
          Common.LABEL_PUBLISHED,
          mediaWritten.publishedDate,
          mediaWritten.publishedDate.toString(),
          ElementInfo.INFO_LEVEL_MINIMUM,
        )
      ),
      mediaWritten.webpage.map { w =>
        ElementInfo(None, MediaPage.VALUE_PUBLICATION_PAGE, Some(Url(w)), ElementInfo.INFO_LEVEL_ALL)
      },
      mediaWritten.articlePage.map { p =>
        ElementInfo(None, MediaPage.VALUE_ARTICLE, Some(Url(p)), ElementInfo.INFO_LEVEL_ALL)
      },
    ).flatten

    ElementCompiledData(
      mediaWritten.designation,
      mediaWritten.title(),
      None,
      Some(mediaWritten.member.mkString(", ")),
      None,
      mediaWritten.description,
      mediaWritten.publishedDate,
      Some(mediaWritten.publishedDate.toString()),
      CoverImage
        .resolve(mediaWritten.coverImage, mediaWritten.designation, mediaWritten.title(), mediaWritten, compilers),
      info,
      UrlResolver.resolve(mediaWritten),
      Media.FROM_KEY,
    )

  }

  override def processPostX(postX: PostX): ElementCompiledData = {

    ElementCompiledData(
      PostX.DESIGNATION_IMAGE,
      Common.EMPTY,
      None,
      None,
      None,
      None,
      postX.publishedDate,
      None,
      null, // TODO: use the X logo ?
      Nil,
      Url(Path(PostX.POST_BASE_URL_1, postX.account, PostX.POST_BASE_URL_2, postX.id.id)),
      PostX.FROM_KEY,
    )

  }

  override def processPostXImage(postXImage: PostXImage): ElementCompiledData = ???

  override def processShow(show: Show): ElementCompiledData = {
    val info = List(
      Some(ElementInfo(Common.LABEL_DATE, show.date.toString(), ElementInfo.INFO_LEVEL_MINIMUM)),
      Some(ElementInfo(ShowPage.LABEL_VENUE, show.location, ElementInfo.INFO_LEVEL_BASIC)),
      show.setlistfm.map { setlistfm =>
        ElementInfo(
          ShowPage.LABEL_SETLIST,
          ShowPage.VALUE_SETLIST,
          Url(ShowPage.URL_SETLISTFM_BASE, setlistfm),
          ElementInfo.INFO_LEVEL_BASIC,
        )
      },
      show.eventPage.map { eventPage =>
        ElementInfo(None, ShowPage.VALUE_EVENT_PAGE, Some(Url(eventPage)), ElementInfo.INFO_LEVEL_ALL)
      },
    ).flatten

    ElementCompiledData(
      ShowPage.DESIGNATION,
      show.fullname,
      show.shortname,
      show.sublabel,
      show.tour.map(compilers.elementDataCompiler.get(_)),
      None,
      show.date,
      Some(show.date.toString()),
      CoverImage.resolve(show.coverImage, ShowPage.DESIGNATION, show.fullname, show, compilers),
      info,
      UrlResolver.resolve(show),
      Show.FROM_KEY,
    )

  }

  override def processSong(song: Song): ElementCompiledData = {

    val info =
      ElementInfo.dateDepending(
        Common.LABEL_EXPECTED,
        Common.LABEL_RELEASED,
        song.releaseDate,
        song.releaseDate.toString(),
        ElementInfo.INFO_LEVEL_MINIMUM,
      ) ::
        song.credits
          .map { credits =>
            List(
              ElementInfo(SongPage.LABEL_LYRICIST, credits.lyricist, ElementInfo.INFO_LEVEL_ALL),
              ElementInfo(SongPage.LABEL_COMPOSER, credits.composer, ElementInfo.INFO_LEVEL_ALL),
            )
          }
          .getOrElse(Nil)

    ElementCompiledData(
      SongPage.DESIGNATION,
      song.fullname,
      None,
      song.fullnameEn,
      song.album.map(id => compilers.elementDataCompiler.get(id)),
      None,
      song.releaseDate,
      None,
      CoverImage.resolve(song.coverImage, SongPage.DESIGNATION, song.fullname, song, compilers),
      info,
      UrlResolver.resolve(song),
      Song.FROM_KEY,
    )

  }

  override def processTour(tour: Tour): ElementCompiledData = {
    val info = List(
      Some(
        ElementInfo(
          TourPage.LABEL_DATES,
          tour.firstDate.toString + TourPage.DATE_RANGE_SEPARATOR + tour.lastDate.toString(),
          ElementInfo.INFO_LEVEL_MINIMUM,
        )
      ),
      tour.eventPage.map { e =>
        ElementInfo(None, TourPage.VALUE_EVENT_PAGE, Some(Url(e)), ElementInfo.INFO_LEVEL_BASIC)
      },
    ).flatten

    ElementCompiledData(
      TourPage.DESIGNATION,
      tour.fullname,
      tour.shortname,
      None,
      None,
      None,
      tour.firstDate,
      None, // TODO: put date range ?
      CoverImage.resolve(tour.coverImage, TourPage.DESIGNATION, tour.fullname, tour, compilers),
      info,
      UrlResolver.resolve(tour),
      Tour.FROM_KEY,
    )

  }

  override def processYouTubeShort(youtubeShort: YouTubeShort): ElementCompiledData = ???

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): ElementCompiledData = ???

  override def processZaiko(zaiko: Zaiko): ElementCompiledData = ???

  override def processChronologyPage(chronologyPage: ChronologyPage): ElementCompiledData = ???

  override def processMusicPage(musicPage: MusicPage): ElementCompiledData = ???

  override def processSite(site: Site): ElementCompiledData = ???

  override def processShowsPage(showsPage: ShowsPage): ElementCompiledData = ???

}
