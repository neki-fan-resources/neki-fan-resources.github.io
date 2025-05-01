package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.neki_site.data.*
import org.skyluc.neki_site.html.pages.SourcesPage.SourceCategory
import org.skyluc.neki_site.html.pages.SourcesPage.SourceEntry
import org.skyluc.neki_site.html.pages.SourcesPage.SourceItem
import org.skyluc.fan_resources.html.CompiledDataGenerator

// TODO: generalize
class SourcesExtractor(generator: CompiledDataGenerator) extends Processor[Seq[SourcesExtractor.DatumEntry]] {

  import SourcesExtractor.*

  override def processAlbumMarker(albumMarker: AlbumMarker): Seq[DatumEntry] = Nil

  // TODO: implement if local image
  override def processBaseMarker(baseMarker: BaseMarker): Seq[DatumEntry] = Nil

  override def processMediaMarker(mediaMarker: MediaMarker): Seq[DatumEntry] = Nil

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Seq[DatumEntry] = Nil

  override def processShowMarker(showMarker: ShowMarker): Seq[DatumEntry] = Nil

  override def processSongMarker(songMarker: SongMarker): Seq[DatumEntry] = Nil

  override def processChronologyPage(chronologyPage: ChronologyPage): Seq[DatumEntry] = Nil

  override def processMusicPage(musicPage: MusicPage): Seq[DatumEntry] = Nil

  override def processSite(site: Site): Seq[DatumEntry] = Nil

  override def processShowsPage(showsPage: ShowsPage): Seq[DatumEntry] = Nil

  override def processAlbum(album: Album): Seq[DatumEntry] = Nil

  override def processLocalImage(localImage: LocalImage): Seq[DatumEntry] = {
    val compiledDate = generator.getElement(localImage.id.itemId)
    Seq(DatumEntry(compiledDate, toSourceEntry(localImage.source, localImage.label.toLowerCase())))
  }

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[DatumEntry] = Nil

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[DatumEntry] = Nil

  override def processPostX(postX: PostX): Seq[DatumEntry] =
    Nil // TODO: associate the usage of images to their item

  override def processPostXImage(postXImage: PostXImage): Seq[DatumEntry] =
    Nil // TODO: associate the usage of images to their item

  override def processPostXVideo(postXVideo: PostXVideo): Seq[DatumEntry] =
    Nil // TODO: associate the usage of images to their item

  override def processShow(show: Show): Seq[DatumEntry] = Nil

  override def processSong(song: Song): Seq[DatumEntry] = {

    val sources = Seq(sourceFromCredits(song.credits)).flatten
      ++ sourcesFromLyrics(song.lyrics)

    val compiledData = generator.getElement(song)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processTour(tour: Tour): Seq[DatumEntry] = Nil

  override def processYouTubeShort(youtubeShort: YouTubeShort): Seq[DatumEntry] = Nil

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Seq[DatumEntry] = Nil

  override def processZaiko(zaiko: Zaiko): Seq[DatumEntry] = Nil

  // ------------------
}

object SourcesExtractor {

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

  val CREDITS_LABEL = "credits"

  private def toSourceEntry(source: Source, label: String): SourceEntry =
    SourceEntry(
      label,
      source.description,
      source.url,
    )

  case class DatumEntry(
      compiledData: ElementCompiledData,
      source: SourceEntry,
  )

  def getAll(datums: Seq[Datum[?]], generator: CompiledDataGenerator): Seq[SourceCategory] = {

    val processor = new SourcesExtractor(generator)

    val res = datums.flatMap { d =>
      d.process(processor)
    }

    val grouped = res.groupBy(_.compiledData.designation)

    val fromDatums = grouped.map { t =>
      SourceCategory(
        t._1,
        t._2
          .groupBy(_.compiledData)
          .map { t2 =>
            SourceItem(
              t2._1.label,
              t2._2.map(_.source).toList,
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
