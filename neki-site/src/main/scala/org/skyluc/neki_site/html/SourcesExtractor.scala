package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.neki_site.data.*
import org.skyluc.neki_site.html.pages.SourcesPage.SourceCategory
import org.skyluc.neki_site.html.pages.SourcesPage.SourceEntry
import org.skyluc.neki_site.html.pages.SourcesPage.SourceItem
// TODO: generalize
class SourcesExtractor(generator: CompiledDataGenerator) extends Processor[Seq[SourcesExtractor.DatumEntry]] {

  import SourcesExtractor.*

  override def processCategoriesPage(categoriesPage: CategoriesPage): Seq[DatumEntry] = Nil

  override def processContentPage(contentPage: ContentPage): Seq[DatumEntry] = Nil

  override def processEvent(event: Event): Seq[DatumEntry] = Nil

  override def processGroup(group: Group): Seq[DatumEntry] = Nil

  override def processMultiMediaEvent(multimediaEvent: MultiMediaEvent): Seq[DatumEntry] = Nil

  override def processSite(site: Site): Seq[DatumEntry] = Nil

  override def processAlbum(album: Album): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(album.coverImage, generator)).flatten

    val compiledData = generator.getElement(album)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processLocalImage(localImage: LocalImage): Seq[DatumEntry] = Nil

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(mediaAudio.coverImage, generator)).flatten

    val compiledData = generator.getElement(mediaAudio)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMediaVideo(mediaVideo: MediaVideo): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(mediaVideo.coverImage, generator)).flatten

    val compiledData = generator.getElement(mediaVideo)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(mediaWritten.coverImage, generator)).flatten

    val compiledData = generator.getElement(mediaWritten)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processNewsItem(newsItem: NewsItem): Seq[DatumEntry] = Nil

  override def processPostX(postX: PostX): Seq[DatumEntry] =
    Nil

  override def processPostXImage(postXImage: PostXImage): Seq[DatumEntry] =
    Nil

  override def processPostXVideo(postXVideo: PostXVideo): Seq[DatumEntry] =
    Nil

  override def processPostYouTube(postYouTube: PostYouTube): Seq[DatumEntry] = Nil

  override def processPostYouTubeImage(postYouTubeImage: PostYouTubeImage): Seq[DatumEntry] = Nil

  override def processShow(show: Show): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(show.coverImage, generator)).flatten

    val compiledData = generator.getElement(show)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processSong(song: Song): Seq[DatumEntry] = {

    val sources = Seq(sourceFromCredits(song.credits), sourceFromCover(song.coverImage, generator)).flatten
      ++ sourcesFromLyrics(song.lyrics)

    val compiledData = generator.getElement(song)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processTour(tour: Tour): Seq[DatumEntry] = {
    val sources = Seq(sourceFromCover(tour.coverImage, generator)).flatten

    val compiledData = generator.getElement(tour)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processUpdatePage(updatePage: UpdatePage): Seq[DatumEntry] = Nil

  override def processWebImage(webImage: WebImage): Seq[DatumEntry] = Nil

  override def processWebPage(webPage: WebPage): Seq[DatumEntry] = Nil

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

  private def sourcesFromLyrics(lyrics: Option[Lyrics]): Seq[SourceEntry] = {
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

  private def sourceFromCover(coverImage: CoverImage, generator: CompiledDataGenerator): Option[SourceEntry] = {
    if (coverImage.fromOtherElement) {
      None
    } else {
      val coverCompiledData = generator.getMultiMedia(coverImage.image)
      Some(
        SourceEntry("cover image", coverCompiledData.sourceDescription, Some(coverCompiledData.targetUrl.toString()))
      )
    }
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
