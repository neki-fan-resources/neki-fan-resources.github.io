package org.skyluc.neki_site.html.pages

import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.fan_resources.data.Song
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html._
import org.skyluc.fan_resources.html.component.LineCard
import org.skyluc.fan_resources.html.component.MainTitle

class SongExtraPage(song: Song, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        compilers.multimediaDataCompiler.getBlock(song),
        Song.FROM_KEY,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(compilers.elementDataCompiler.get(song))
      )
    )
      ++ mediaSection

  }
}
