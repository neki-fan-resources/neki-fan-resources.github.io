package org.skyluc.neki_site.html.compileddata

import org.skyluc.fan_resources.data.{ProcessorElement as _, *}
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.data.*
import org.skyluc.neki_site.html.page.SourcesPage.SourceCategory
import org.skyluc.neki_site.html.page.SourcesPage.SourceEntry
import org.skyluc.neki_site.html.page.SourcesPage.SourceItem
// TODO: generalize
class SourcesExtractor(generator: fr.compileddata.CompiledDataGenerator)
    extends ProcessorElement[Seq[SourcesExtractor.DatumEntry]] {

  import SourcesExtractor.*

  override def processCategoriesPage(categoriesPage: CategoriesPage): Seq[DatumEntry] = Nil

  override def processContentPage(contentPage: ContentPage): Seq[DatumEntry] = Nil

  override def processEvent(event: Event): Seq[DatumEntry] = {
    val sources = sourcesForElement(event, generator)

    val compiledData = generator.getElementCompiledData(event)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMultiMediaEvent(multimediaEvent: MultiMediaEvent): Seq[DatumEntry] = Nil

  override def processAlbum(album: Album): Seq[DatumEntry] = {
    val sources = sourcesForElement(album, generator)

    val compiledData = generator.getElementCompiledData(album)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[DatumEntry] = {
    val sources = sourcesForElement(mediaAudio, generator)

    val compiledData = generator.getElementCompiledData(mediaAudio)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMediaVideo(mediaVideo: MediaVideo): Seq[DatumEntry] = {
    val sources = sourcesForElement(mediaVideo, generator)

    val compiledData = generator.getElementCompiledData(mediaVideo)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[DatumEntry] = {
    val sources = sourcesForElement(mediaWritten, generator)

    val compiledData = generator.getElementCompiledData(mediaWritten)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processShow(show: Show): Seq[DatumEntry] = {
    val sources = sourcesForElement(show, generator)

    val compiledData = generator.getElementCompiledData(show)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  override def processSong(song: Song): Seq[DatumEntry] = {
    if (song.id.dark) {
      Nil
    } else {
      val sources = sourcesForElement(song, generator)
        ++ song.lyrics.flatMap(lyricsId => sourcesFromLyrics(generator.get(lyricsId)))

      val compiledData = generator.getElementCompiledData(song)

      sources.map { s => DatumEntry(compiledData, s) }
    }

  }

  override def processTour(tour: Tour): Seq[DatumEntry] = {
    val sources = sourcesForElement(tour, generator)

    val compiledData = generator.getElementCompiledData(tour)

    sources.map { s => DatumEntry(compiledData, s) }
  }

  // ------------------
}

object SourcesExtractor {

  private def sourcesFromLyrics(lyrics: Lyrics): Seq[SourceEntry] = {
    lyrics.languages.flatMap { ll =>
      ll.source.map { s =>
        toSourceEntry(s, ll.name)
      }
    }
  }

  private def sourcesForElement(
      element: Element[?],
      generator: fr.compileddata.CompiledDataGenerator,
  ): Seq[SourceEntry] = {
    val attributeSources = element.attributes.infoAttributes.flatMap { attribute =>
      attribute.source.map(s => toSourceEntry(s, attribute.definition.label))
    }

    val coverCompiledData = generator.getMultimediaCompiledData(element.coverImage)
    val coverSource =
      SourceEntry("cover image", coverCompiledData.sourceDescription, Some(coverCompiledData.targetUrl))

    attributeSources :+ coverSource
  }

  val CREDITS_LABEL = "credits"

  private def toSourceEntry(source: Source, label: String): SourceEntry =
    SourceEntry(
      label,
      source.description,
      source.url,
    )

  case class DatumEntry(
      compiledData: fr.compileddata.ElementCompiledData,
      source: SourceEntry,
  )

  def getAll(elements: Seq[Element[?]], generator: fr.compileddata.CompiledDataGenerator): Seq[SourceCategory] = {

    val processor = new SourcesExtractor(generator)

    val res = elements.flatMap { d =>
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
