package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LineCard
import org.skyluc.fan_resources.html.component.MainTitle
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.fan_resources.data.Tour

class TourExtraPage(
    tour: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        multimediaBlock,
        Tour.FROM_KEY,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(tour)
      )
    )
      ++ mediaSection

  }
}
