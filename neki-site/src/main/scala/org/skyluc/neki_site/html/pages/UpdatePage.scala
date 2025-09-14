package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.UpdateCompiledData
import org.skyluc.fan_resources.html.component.UpdatesSection
import org.skyluc.html.*
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.fan_resources.html.TextCompiledData

class UpdatePage(updates: Seq[UpdateCompiledData], pageDescription: PageDescription, site: d.Site)
    extends SitePage(pageDescription, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    UpdatesSection.generate(updates)
  }

}

object UpdatePage {

  def pages(updatePage: dfr.UpdatePage, site: d.Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val path = Path(updatePage.id.id)

    val updates = TextCompiledData.toCompiledData(updatePage.id, updatePage.updates, generator)

    val pageDescription =
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          updatePage.label,
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          updatePage.label,
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(generator.getMultiMedia(updatePage.coverImage.image).image.source),
        SitePage.canonicalUrlFor(path),
        path.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        updatePage.id.dark,
      )

    Seq(UpdatePage(updates, pageDescription, site))
  }

}
