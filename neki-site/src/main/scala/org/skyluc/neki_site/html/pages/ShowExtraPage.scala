package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.data._
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html.BodyElement
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.MainTitle
import org.skyluc.fan_resources.html.component.LineCard

class ShowExtraPage(show: Show, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        compilers.multimediaDataCompiler.get(show.multimedia, show.linkedTo),
        Show.FROM_KEY,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(compilers.elementDataCompiler.get(show))
      )
    )
      ++ mediaSection
  }
}
