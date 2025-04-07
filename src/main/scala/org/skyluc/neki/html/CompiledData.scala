package org.skyluc.neki.html

import scala.collection.immutable.HashMap
import org.skyluc.neki.data.{Pages => _, *}
import org.skyluc.neki.html.page.AlbumPage
import org.skyluc.neki.html.page.SongPage
import org.skyluc.neki.html.page.ShowPage
import org.skyluc.neki.html.page.TourPage
import org.skyluc.neki.html.page.MediaPage

case class ItemCompiledData(
    url: String,
    designation: String,
    label: String,
    shortLabel: Option[String],
    sublabel: Option[String],
    parent: Option[ItemCompiledData],
    date: Date,
    displayDate: Option[String],
    coverUrl: String,
    coverAlt: String,
    info: List[ItemInfo],
    fromKey: String,
    missing: Boolean = false,
) {
  def labelWithSublabel(): String = {
    val sublabelString = sublabel.map(sl => s" ($sl)").getOrElse(CommonBase.EMPTY)
    label + sublabelString
  }
}

object ItemCompiledData {
  def missing(id: Id[?]): ItemCompiledData = {
    println(s"***** ERROR: '$id' not found.")
    ItemCompiledData(
      CommonBase.EMPTY,
      Pages.TEXT_PLACEHOLDER,
      id.toString(),
      None,
      None,
      None,
      OLD_DATE,
      None,
      CommonBase.EMPTY,
      CommonBase.EMPTY,
      Nil,
      "undefined",
      true,
    )
  }

  val OLD_DATE = Date(2023, 1, 1)
}

case class ItemInfo(
    label: Option[String],
    value: String,
    url: Option[String],
    infoLevel: Int,
)

object ItemInfo {
  def apply(label: String, value: String, url: String, infoLevel: Int): ItemInfo =
    ItemInfo(Some(label), value, Some(url), infoLevel)
  def apply(label: String, value: String, infoLevel: Int): ItemInfo =
    ItemInfo(Some(label), value, None, infoLevel)

  val INFO_LEVEL_NONE = 0
  val INFO_LEVEL_MINIMUM = 1
  val INFO_LEVEL_BASIC = 2
  val INFO_LEVEL_ALL = 3
}

case class MultiMediaCompiledData(
    url: String,
    imageUrl: String,
    designation: String,
    label: String,
    info: Option[String],
    date: Date,
    from: List[(String, ItemCompiledData)],
    overlay: String,
    available: Boolean,
)

case class MultiMediaCompiledDataWithParentKey(
    compiledData: MultiMediaCompiledData,
    parentKey: String,
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
      true,
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
  def multimedia: Option[MultiMediaCompiledDataWithParentKey]
  def item: Option[ItemCompiledData]
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
  val LABEL_PUBLISHED = "published"
  val LABEL_DATE = "date"

  var cache: Map[Id[?], ItemCompiledData] = HashMap()
  var cache2: Map[Id[?], MultiMediaCompiledData] = HashMap()

  def getItem(id: Id[?], data: Data): ItemCompiledData = {
    // TODO: push cleanly in Id trait
    id match {
      case a: AlbumId => getAlbum(a, data)
      case s: ShowId  => getShow(s, data)
      case s: SongId  => getSong(s, data)
      case t: TourId  => getTour(t, data)
      case _ =>
        ItemCompiledData.missing(id)
    }
  }

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

  def getMedia(id: MediaId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForMedia(id, data)
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
      List(ItemInfo(CompiledData.LABEL_RELEASED, album.releaseDate.toString(), ItemInfo.INFO_LEVEL_MINIMUM))
    ItemCompiledData(
      Album.URL_BASE + album.id.id + Pages.HTML_EXTENSION,
      album.designation,
      album.fullname,
      None,
      None,
      None,
      album.releaseDate,
      None,
      CoverImage.resolveUrl(album.coverImage, album, data),
      CoverImage.buildAlt(AlbumPage.DESIGNATION, album.fullname),
      info,
      Album.FROM_KEY,
    )
  }

  def compileForMedia(id: MediaId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.medias, data)(compileForMedia)
  }

  def compileForMedia(media: Media, data: Data): ItemCompiledData = {
    val info = List(
      Some(ItemInfo(MediaPage.LABEL_HOST, media.host, ItemInfo.INFO_LEVEL_BASIC)),
      Some(ItemInfo(CompiledData.LABEL_PUBLISHED, media.publishedDate.toString(), ItemInfo.INFO_LEVEL_MINIMUM)),
      media.webpage.map { w =>
        ItemInfo(None, MediaPage.VALUE_SHOW_PAGE, Some(w), ItemInfo.INFO_LEVEL_ALL)
      },
      media.program.map { p =>
        ItemInfo(None, MediaPage.VALUE_PROGRAM, Some(p), ItemInfo.INFO_LEVEL_ALL)
      },
    ).flatten

    ItemCompiledData(
      Media.URL_BASE + media.id.year + Pages.HTML_SEPARATOR + media.id.id + Pages.HTML_EXTENSION,
      MediaPage.DESIGNATION,
      media.radio + " - " + media.show,
      None,
      Some(media.member.mkString(", ")),
      None,
      media.publishedDate,
      Some(media.publishedDate.toString()),
      CoverImage.resolveUrl(media.coverImage, media, data),
      CoverImage.buildAlt(MediaPage.DESIGNATION, media.radio + " - " + media.show),
      info,
      Media.FROM_KEY,
    )
  }

  def compileForMultiMedia(id: MultiMediaId, data: Data): MultiMediaCompiledData = {
    data.multimedia
      .get(id)
      .map {
        case y: YouTubeShort =>
          compileForYouTubeShort(y, data)
        case y: YouTubeVideo =>
          compileForYouTubeVideo(y, data)
        case z: Zaiko =>
          compileForZaiko(z, data)
      }
      .getOrElse(MultiMediaCompiledData.missing(id))
  }

  def compileForShow(id: ShowId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.shows, data)(compileForShow)
  }

  def compileForShow(show: Show, data: Data): ItemCompiledData = {
    val info = List(
      Some(ItemInfo(CompiledData.LABEL_DATE, show.date.toString(), ItemInfo.INFO_LEVEL_MINIMUM)),
      Some(ItemInfo(ShowPage.LABEL_VENUE, show.location, ItemInfo.INFO_LEVEL_BASIC)),
      show.setlistfm.map { s =>
        ItemInfo(
          ShowPage.LABEL_SETLIST,
          ShowPage.VALUE_SETLIST,
          ShowPage.URL_SETLISTFM_BASE + s,
          ItemInfo.INFO_LEVEL_BASIC,
        )
      },
      show.eventPage.map { e => ItemInfo(None, ShowPage.VALUE_EVENT_PAGE, Some(e), ItemInfo.INFO_LEVEL_ALL) },
    ).flatten

    ItemCompiledData(
      Show.URL_BASE + show.id.year + Pages.HTML_SEPARATOR + show.id.id + Pages.HTML_EXTENSION,
      ShowPage.DESIGNATION,
      show.fullname,
      show.shortname,
      show.sublabel,
      show.tour.map(CompiledData.getTour(_, data)),
      show.date,
      Some(show.date.toString()),
      CoverImage.resolveUrl(show.coverImage, show, data),
      CoverImage.buildAlt(ShowPage.DESIGNATION, show.fullname),
      info,
      Show.FROM_KEY,
    )
  }

  def compileForSong(id: SongId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.songs, data)(compileForSong)
  }

  def compileForSong(song: Song, data: Data): ItemCompiledData = {
    val info =
      ItemInfo(CompiledData.LABEL_RELEASED, song.releaseDate.toString(), ItemInfo.INFO_LEVEL_MINIMUM) ::
        song.credits
          .map { credits =>
            List(
              ItemInfo(SongPage.LABEL_LYRICIST, credits.lyricist, ItemInfo.INFO_LEVEL_ALL),
              ItemInfo(SongPage.LABEL_COMPOSER, credits.composer, ItemInfo.INFO_LEVEL_ALL),
            )
          }
          .getOrElse(Nil)

    ItemCompiledData(
      Song.URL_BASE + song.id.id + Pages.HTML_EXTENSION,
      SongPage.DESIGNATION,
      song.fullname,
      None,
      song.fullnameEn,
      song.album.map(getAlbum(_, data)),
      song.releaseDate,
      None,
      CoverImage.resolveUrl(song.coverImage, song, data),
      CoverImage.buildAlt(SongPage.DESIGNATION, song.fullname),
      info,
      Song.FROM_KEY,
    )
  }

  def compileForTour(id: TourId, data: Data): ItemCompiledData = {
    compileOrMissingItem(id, data.tours, data)(compileForTour)
  }

  def compileForTour(tour: Tour, data: Data): ItemCompiledData = {
    val info = List(
      Some(
        ItemInfo(
          TourPage.LABEL_DATES,
          tour.firstDate.toString + DATE_RANGE_SEPARATOR + tour.lastDate.toString(),
          ItemInfo.INFO_LEVEL_MINIMUM,
        )
      ),
      tour.eventPage.map { e => ItemInfo(None, TourPage.VALUE_EVENT_PAGE, Some(e), ItemInfo.INFO_LEVEL_BASIC) },
    ).flatten

    ItemCompiledData(
      Tour.URL_BASE + tour.id.id + Pages.HTML_EXTENSION,
      TourPage.DESIGNATION,
      tour.fullname,
      tour.shortname,
      None,
      None, // TODO: link to tour
      tour.firstDate,
      None, // TODO: put date range ?
      CoverImage.resolveUrl(tour.coverImage, tour, data),
      CoverImage.buildAlt(TourPage.DESIGNATION, tour.fullname),
      info,
      Tour.FROM_KEY,
    )
  }

  def compileForYouTubeShort(youtubeshort: YouTubeShort, data: Data): MultiMediaCompiledData = {
    MultiMediaCompiledData(
      youtubeshort.url(),
      youtubeshort.imageUrl(),
      "YouTube Short",
      youtubeshort.label,
      youtubeshort.info,
      youtubeshort.publishedDate,
      youtubeshort.relatedTo
        .map { id =>
          val item = getItem(id, data)
          (item.fromKey, item)
        },
      YouTubeVideo.OVERLAY_FILE,
      true,
    )
  }

  def compileForYouTubeVideo(youtubevideo: YouTubeVideo, data: Data): MultiMediaCompiledData = {
    MultiMediaCompiledData(
      youtubevideo.url(),
      youtubevideo.imageUrl(),
      "YouTube Video",
      youtubevideo.label,
      None, // TODO: needed at some point ?
      youtubevideo.publishedDate,
      youtubevideo.relatedTo
        .map { id =>
          val item = getItem(id, data)
          (item.fromKey, item)
        },
      YouTubeVideo.OVERLAY_FILE,
      true,
    )
  }

  def compileForZaiko(zaiko: Zaiko, data: Data): MultiMediaCompiledData = {
    val info = "available from " + zaiko.publishedDate.toString() +
      zaiko.expirationDate.map(d => " until " + d.toString()).getOrElse(CommonBase.EMPTY)
    val available = zaiko.expirationDate.map(_.isPast()).getOrElse(true)
    MultiMediaCompiledData(
      zaiko.url(),
      zaiko.coverImage,
      "Zaiko",
      zaiko.label,
      Some(info),
      zaiko.publishedDate,
      zaiko.relatedTo
        .map { id =>
          val item = getItem(id, data)
          (item.fromKey, item)
        },
      Zaiko.OVERLAY_FILE,
      available,
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
