package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.data.Media
import org.skyluc.fan_resources.html.component.LineCard
import org.skyluc.fan_resources.html.component.MainTitle
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.*
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage

class MediaExtraPage(media: Media, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        compilers.multimediaDataCompiler.get(media.multimedia, media.linkedTo),
        Media.FROM_KEY,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(compilers.elementDataCompiler.get(media))
      )
    )
      ++ mediaSection

  }
}
