package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object AlbumPage {
  def pagesFor(album: d.Album, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(album)

    val songsSection = if (album.songs.isEmpty) {
      Nil
    } else {
      Seq(
        fr.page.ContentSectionChronology(
          "Songs",
          album.songs.map(generator.getElementCompiledData(_)),
        )
      )
    }

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        songsSection ++ fr.compileddata.MultimediaCompiledData.getMainContentSections(album, generator),
      )
    )
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(album, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      album.designation,
      None,
      album.fullname,
      None,
      None,
      album.altname,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
