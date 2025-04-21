package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.data.Album
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html._
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.MainTitle
import org.skyluc.fan_resources.html.component.LineCard

class AlbumExtraPage(album: Album, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        compilers.multimediaDataCompiler.get(album.multimedia, album.linkedTo),
        Album.FROM_KEY,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(compilers.elementDataCompiler.get(album))
      )
    )
      ++ mediaSection
  }
}

object AlbumExtraPage {}
