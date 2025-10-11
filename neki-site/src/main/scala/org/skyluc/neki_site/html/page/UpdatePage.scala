package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.BodyElement

import dfr.Path

class UpdatePage(
    updatePage: fr.compileddata.ElementCompiledData,
    updates: Seq[fr.compileddata.ElementUpdateCompiledData],
) extends MainSitePage {

  override val outputPath: Path = updatePage.id.path.tailSegments().withExtension(Common.HTML_EXTENSION)

  override val pageConfiguration: org.skyluc.fan_resources.html.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle(updatePage.label),
      TitleGenerator.generateDescription(updatePage.label),
      outputPath.toAbsoluteString(),
      updatePage.cover.imageUrl,
      false,
    )

  override def mainContent(): Seq[BodyElement[?]] =
    fr.component.ElementUpdatesSection.generate(updates)

}

object UpdatePage {

  def pagesFor(
      updatePage: dfr.UpdatePage,
      generator: fr.compileddata.CompiledDataGenerator,
  ): Seq[fr.page.MainSitePage] = {
    val compiledData = generator.getElementCompiledData(updatePage)

    val updates = generator.getWithPrefix[dfr.ElementUpdate](dfr.ElementUpdate.ID_BASE)
    val updatesCompiledData = updates.map(fr.compileddata.ElementUpdateCompiledData.from(_, generator))

    Seq(
      UpdatePage(compiledData, updatesCompiledData)
    )
  }
}
