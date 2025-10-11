package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object SongPage {
  def pagesFor(song: d.Song, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(song)

    val lyrics = song.lyrics.headOption.map(generator.get(_))

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        fr.compileddata.MultimediaCompiledData.getMainContentSections(song, generator),
      )
    )
      ++ lyrics.map { fr.page.ContentBlockLyrics(_) }
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(song, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      d.Song.DESIGNATION,
      None,
      song.fullname,
      song.fullnameEn,
      song.shortName,
      None,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
