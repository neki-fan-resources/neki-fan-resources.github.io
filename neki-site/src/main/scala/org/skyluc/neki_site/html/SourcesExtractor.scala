package org.skyluc.neki_site.html

import org.skyluc.neki_site.data.Processor
import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Source
import org.skyluc.neki_site.data.ShowsPage
import org.skyluc.fan_resources.data.MediaWritten
import org.skyluc.fan_resources.data.LocalImage
import org.skyluc.fan_resources.data.YouTubeShort
import org.skyluc.fan_resources.data.PostX
import org.skyluc.fan_resources.data.YouTubeVideo
import org.skyluc.fan_resources.data.Tour
import org.skyluc.neki_site.data.ChronologyPage
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.SourcesExtractor.DatumEntry
import org.skyluc.neki_site.data.MusicPage
import org.skyluc.fan_resources.data.Song
import org.skyluc.fan_resources.data.Show
import org.skyluc.fan_resources.data.Album
import org.skyluc.fan_resources.data.PostXImage
import org.skyluc.fan_resources.data.Zaiko
import org.skyluc.fan_resources.data.MediaAudio
import org.skyluc.neki_site.html.pages.SourcesPage.SourceCategory
import org.skyluc.neki_site.html.pages.SourcesPage.SourceItem
import org.skyluc.neki_site.html.pages.SourcesPage.SourceEntry
import org.skyluc.fan_resources.data.FileCoverImage
import org.skyluc.neki_site.html.pages.ShowPage
import org.skyluc.fan_resources.data.Credits
import org.skyluc.neki_site.html.pages.SongPage
import org.skyluc.fan_resources.data.Lyrics
import org.skyluc.neki_site.html.pages.TourPage
import org.skyluc.fan_resources.data.BaseMarker
import org.skyluc.fan_resources.data.AlbumMarker
import org.skyluc.fan_resources.data.MultiMediaMarker
import org.skyluc.fan_resources.data.ShowMarker
import org.skyluc.fan_resources.data.SongMarker
import org.skyluc.fan_resources.data.MediaMarker

object SourcesExtractor extends Processor[Option[DatumEntry]] {

  override def processAlbumMarker(albumMarker: AlbumMarker): Option[DatumEntry] = ???

  // TODO: implement if local image
  override def processBaseMarker(baseMarker: BaseMarker): Option[DatumEntry] = ???

  override def processMediaMarker(mediaMarker: MediaMarker): Option[DatumEntry] = ???

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Option[DatumEntry] = ???

  override def processShowMarker(showMarker: ShowMarker): Option[DatumEntry] = ???

  override def processSongMarker(songMarker: SongMarker): Option[DatumEntry] = ???

  override def processChronologyPage(chronologyPage: ChronologyPage): Option[DatumEntry] = None

  override def processMusicPage(musicPage: MusicPage): Option[DatumEntry] = None

  override def processSite(site: Site): Option[DatumEntry] = None

  override def processShowsPage(showsPage: ShowsPage): Option[DatumEntry] = None

  override def processAlbum(album: Album): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(album.coverImage)).flatten

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(album.designation, album, sources))
    }

  }

  override def processLocalImage(localImage: LocalImage): Option[DatumEntry] =
    None // TODO: associate with its item ?

  override def processMediaAudio(mediaAudio: MediaAudio): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(mediaAudio.coverImage)).flatten

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(mediaAudio.designation, mediaAudio, sources))
    }
  }

  override def processMediaWritten(mediaWritten: MediaWritten): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(mediaWritten.coverImage)).flatten

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(mediaWritten.designation, mediaWritten, sources))
    }
  }

  override def processPostX(postX: PostX): Option[DatumEntry] =
    None // TODO: associate the usage of images to their item

  override def processPostXImage(postXImage: PostXImage): Option[DatumEntry] =
    None // TODO: associate the usage of images to their item

  override def processShow(show: Show): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(show.coverImage)).flatten

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(ShowPage.DESIGNATION, show, sources))
    }
  }

  override def processSong(song: Song): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(song.coverImage), sourceFromCredits(song.credits)).flatten
      ++ sourcesFromLyrics(song.lyrics)

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(SongPage.DESIGNATION, song, sources))
    }
  }

  override def processTour(tour: Tour): Option[DatumEntry] = {
    val sources = Seq(sourceFromCoverImage(tour.coverImage)).flatten

    if (sources.isEmpty) {
      None
    } else {
      Some(DatumEntry(TourPage.DESIGNATION, tour, sources))
    }
  }

  override def processYouTubeShort(youtubeShort: YouTubeShort): Option[DatumEntry] = None

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Option[DatumEntry] = None

  override def processZaiko(zaiko: Zaiko): Option[DatumEntry] = None

  // ------------------

  private def sourceFromCoverImage(coverImage: org.skyluc.fan_resources.data.CoverImage): Option[SourceEntry] = {
    coverImage match {
      case FileCoverImage(_, source) =>
        Some(toSourceEntry(source, COVER_IMAGE_LABEL))
      case _ =>
        None
    }
  }

  private def sourceFromCredits(credits: Option[Credits]): Option[SourceEntry] = {
    credits.flatMap { c =>
      c.source.map { s =>
        toSourceEntry(s, CREDITS_LABEL)

      }
    }
  }

  private def sourcesFromLyrics(lyrics: Option[Lyrics]): List[SourceEntry] = {
    lyrics
      .map { l =>
        l.languages.flatMap { ll =>
          ll.source.map { s =>
            toSourceEntry(s, ll.name)
          }
        }
      }
      .getOrElse(Nil)
  }

  val COVER_IMAGE_LABEL = "cover image"
  val CREDITS_LABEL = "credits"

  private def toSourceEntry(source: Source, label: String): SourceEntry =
    SourceEntry(
      label,
      source.description,
      source.url,
    )

  case class DatumEntry(
      designation: String,
      datum: Datum[?],
      sources: Seq[SourceEntry],
  )

  def getAll(compilers: Compilers): Seq[SourceCategory] = {

    val res = compilers.data.all.values.flatMap { d =>
      d.process(this)
    }

    val grouped = res.groupBy(_.designation)

    val fromDatums = grouped.map { t =>
      SourceCategory(
        t._1,
        t._2
          .map { de =>
            SourceItem(
              compilers.elementDataCompiler.get(de.datum).label,
              de.sources.toList,
            )
          }
          .toList
          .sortBy(_.label),
      )
    }.toSeq

    val withStatic =
      SourceCategory(
        "Pages",
        List(
          SourceItem(
            "Band",
            List(
              SourceEntry(
                "Band image",
                "frame from 'Get Over' music video",
                Some("https://www.youtube.com/watch?v=TWbLqba3oag"),
              )
            ),
          )
        ),
      ) +: fromDatums

    withStatic.sortBy(_.label)
  }
}
