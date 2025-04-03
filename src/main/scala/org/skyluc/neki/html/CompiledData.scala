package org.skyluc.neki.html

import scala.collection.immutable.HashMap
import org.skyluc.neki.data.{Pages as dPages, *}
import org.skyluc.neki.html.page.AlbumPage
import org.skyluc.neki.html.page.SongPage
import org.skyluc.neki.html.page.ShowPage
import org.skyluc.neki.html.page.TourPage
import org.skyluc.neki.html.page.ChronologySvg

case class ItemCompiledData(
    url: String,
    designation: String,
    label: String,
    sublabel: Option[String],
    parent: Option[ItemCompiledData],
    date: Date,
    displayDate: Option[String],
    coverUrl: String,
    coverAlt: String,
    info: List[ItemInfo],
    missing: Boolean = false,
)

object ItemCompiledData {
  def missing(id: Id[?]): ItemCompiledData = {
    println(s"***** ERROR: '$id' not found.")
    ItemCompiledData(
      CommonBase.EMPTY,
      Pages.TEXT_PLACEHOLDER,
      id.toString(),
      None,
      None,
      OLD_DATE,
      None,
      CommonBase.EMPTY,
      CommonBase.EMPTY,
      Nil,
      true,
    )
  }

  val OLD_DATE = Date(2023, 1, 1)
}

case class ItemInfo(
    label: Option[String],
    value: String,
    url: Option[String],
)

object ItemInfo {
  def apply(label: String, value: String, url: String): ItemInfo =
    ItemInfo(Some(label), value, Some(url))
  def apply(label: String, value: String): ItemInfo =
    ItemInfo(Some(label), value, None)
}

case class MultiMediaCompiledData(
    url: String,
    imageUrl: String,
    designation: String,
    label: String,
    info: Option[String],
    date: Date,
    from: List[(String, String)],
    overlay: String,
)

object MultiMediaCompiledData {
  def missing(id: Id[?]): MultiMediaCompiledData = {
    println(s"***** ERROR: '$id' not found.")
    MultiMediaCompiledData(
      CommonBase.EMPTY,
      CommonBase.EMPTY,
      Pages.TEXT_PLACEHOLDER,
      id.toString(),
      None,
      OLD_DATE,
      Nil,
      CommonBase.EMPTY,
    )
  }

  val OLD_DATE = Date(2023, 1, 1)
}

trait MarkerCompiledData {
  def id: String
  def designation: Option[String]
  def label: String
  def sublabel: Option[String]
  def image: Option[String]
  def imageAlt: Option[String]
  def day: Int
  def left: Boolean
  def short: Boolean
  def `class`: String
  def up: Int
  def in: Int
}

// ----------------

type ItemCompiledDataTree = List[ItemCompiledDataNode]

case class ItemCompiledDataNode(
    data: ItemCompiledData,
    subNodes: ItemCompiledDataTree,
)

// ----------------

object CompiledData {

  val LABEL_RELEASED = "released"
  val LABEL_DATE = "date"

  var cache: Map[Id[?], ItemCompiledData] = HashMap()
  var cache2: Map[Id[?], MultiMediaCompiledData] = HashMap()

  def getAlbum(id: AlbumId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForAlbum(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def getMultiMedia(ids: List[MultiMediaId], data: Data): List[MultiMediaCompiledData] = {
    ids.map(getMultiMedia(_, data))
  }

  def getMultiMedia(id: MultiMediaId, data: Data): MultiMediaCompiledData = {
    cache2.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForMultiMedia(id, data)
        cache2 += ((id, compiledData))
        compiledData
    }
  }

  def getShow(id: ShowId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForShow(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def getSong(id: SongId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForSong(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def getTour(id: TourId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForTour(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def compileForAlbum(id: AlbumId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.albums, data)(compileForAlbum)
  }

  def compileForAlbum(album: Album, data: Data): ItemCompiledData = {
    val info =
      List(ItemInfo(CompiledData.LABEL_RELEASED, album.releaseDate.toString()))
    ItemCompiledData(
      Album.URL_BASE + album.id.id + Pages.HTML_EXTENSION,
      album.designation,
      album.fullname,
      None,
      None,
      album.releaseDate,
      None,
      CoverImage.resolveUrl(album.coverImage, album, data),
      CoverImage.buildAlt(AlbumPage.DESIGNATION, album.fullname),
      info,
    )
  }

  def compileForMultiMedia(id: MultiMediaId, data: Data): MultiMediaCompiledData = {
    data.multimedia
      .get(id)
      .map { case y: YouTubeVideo =>
        compileForYouTubeVideo(y, data)
      }
      .getOrElse(MultiMediaCompiledData.missing(id))
  }

  def compileForShow(id: ShowId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.shows, data)(compileForShow)
  }

  def compileForShow(show: Show, data: Data): ItemCompiledData = {
    val info = List(
      Some(ItemInfo(CompiledData.LABEL_DATE, show.date.toString())),
      Some(ItemInfo(ShowPage.LABEL_VENUE, show.location)),
      show.setlistfm.map { s =>
        ItemInfo(ShowPage.LABEL_SETLIST, ShowPage.VALUE_SETLIST, ShowPage.URL_SETLISTFM_BASE + s)
      },
      show.eventPage.map { e => ItemInfo(None, ShowPage.VALUE_EVENT_PAGE, Some(e)) },
    ).flatten

    ItemCompiledData(
      Show.URL_BASE + show.id.year + Pages.HTML_SEPARATOR + show.id.id + Pages.HTML_EXTENSION,
      ShowPage.DESIGNATION,
      show.fullname,
      show.sublabel,
      show.tour.map(CompiledData.getTour(_, data)),
      show.date,
      Some(show.date.toString()),
      CoverImage.resolveUrl(show.coverImage, show, data),
      CoverImage.buildAlt(ShowPage.DESIGNATION, show.fullname),
      info,
    )
  }

  def compileForSong(id: SongId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.songs, data)(compileForSong)
  }

  def compileForSong(song: Song, data: Data): ItemCompiledData = {
    val info =
      ItemInfo(CompiledData.LABEL_RELEASED, song.releaseDate.toString()) ::
        song.credits
          .map { credits =>
            List(
              ItemInfo(SongPage.LABEL_LYRICIST, credits.lyricist),
              ItemInfo(SongPage.LABEL_COMPOSER, credits.composer),
            )
          }
          .getOrElse(Nil)

    ItemCompiledData(
      Song.URL_BASE + song.id.id + Pages.HTML_EXTENSION,
      SongPage.DESIGNATION,
      song.fullname,
      song.fullnameEn,
      song.album.map(getAlbum(_, data)),
      song.releaseDate,
      None,
      CoverImage.resolveUrl(song.coverImage, song, data),
      CoverImage.buildAlt(SongPage.DESIGNATION, song.fullname),
      info,
    )
  }

  def compileForTour(id: TourId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.tours, data)(compileForTour)
  }

  def compileForTour(tour: Tour, data: Data): ItemCompiledData = {
    val info = List(
      Some(ItemInfo(TourPage.LABEL_DATES, tour.firstDate.toString + DATE_RANGE_SEPARATOR + tour.lastDate.toString())),
      tour.eventPage.map { e => ItemInfo(None, TourPage.VALUE_EVENT_PAGE, Some(e)) },
    ).flatten

    ItemCompiledData(
      Tour.URL_BASE + tour.id.id + Pages.HTML_EXTENSION,
      TourPage.DESIGNATION,
      tour.fullname,
      None,
      None, // TODO: link to tour
      tour.firstDate,
      None, // TODO: put date range ?
      CoverImage.resolveUrl(tour.coverImage, tour, data),
      CoverImage.buildAlt(TourPage.DESIGNATION, tour.fullname),
      info,
    )
  }

  def compileForYouTubeVideo(youtubevideo: YouTubeVideo, data: Data): MultiMediaCompiledData = {
    MultiMediaCompiledData(
      youtubevideo.url(),
      youtubevideo.imageUrl(),
      youtubevideo.label,
      "YouTube Video",
      None, // TODO: needed at some point ?
      youtubevideo.publishedDate,
      Nil, // TODO: add, accordingly to the content of relatedTo
      YouTubeVideo.OVERLAY_FILE,
    )
  }

  def compileOrMissingItem[T](id: Id[T], map: Map[Id[T], T], data: Data)(
      compile: (T, Data) => ItemCompiledData
  ): ItemCompiledData = {
    map.get(id).map(compile(_, data)).getOrElse(ItemCompiledData.missing(id))
  }

  // ------------
  val DATE_RANGE_SEPARATOR = " - "
}
