package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*

object MainSitePageBuilder extends fr.page.MainSitePageBuilder {
  def build(
      element: fr.compileddata.ElementCompiledData,
      outputPath: Path,
      content: Seq[BodyElement[?]],
  ): fr.page.MainSitePage = {
    MainSitePageBuilder.SitePage(element, outputPath, content)
  }

  class SitePage(
      element: fr.compileddata.ElementCompiledData,
      override val outputPath: Path,
      content: Seq[BodyElement[?]],
  ) extends MainSitePage {

    override val pageConfiguration: org.skyluc.fan_resources.html.page.MainSitePageConfiguration = {
      val isRoot = outputPath == INDEX_PATH

      MainSitePageConfiguration(
        TitleGenerator.generateTitle(element.label),
        TitleGenerator.generateDescription(element.label),
        if (isRoot) "/" else outputPath.toAbsoluteString(),
        element.cover.imageUrl,
        isRoot,
      )
    }

    override def mainContent(): Seq[BodyElement[?]] = content

  }

  val INDEX_PATH = Path("index.html")
}
