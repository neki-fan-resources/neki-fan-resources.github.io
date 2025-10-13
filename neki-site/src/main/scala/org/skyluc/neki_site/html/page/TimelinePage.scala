package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.html.*

import dfr.Path

class TimelinePage(
    contentPage: fr.compileddata.ElementCompiledData,
    configuration: component.TimelineSectionConfiguration,
) extends MainSitePage {

  override val outputPath: Path = contentPage.id.path.tailSegments().withExtension(Common.HTML_EXTENSION)

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle(contentPage.label),
      TitleGenerator.generateDescription(contentPage.label),
      outputPath.toAbsoluteString(),
      contentPage.cover.imageUrl,
    )

  override def mainContent(): Seq[BodyElement[?]] = {
    contentPage.description.map { d =>
      fr.component.MainIntro.generate(d)
    }.toSeq
      ++ component.TimelineSection.generate(configuration)
  }

}

object TimelinePage {

  def pagesFor(
      contentPage: dfr.ContentPage,
      generator: fr.compileddata.CompiledDataGenerator,
  ): Seq[fr.page.MainSitePage] = {
    val compiledData = generator.getElementCompiledData(contentPage)

    val configuration = component.TimelineSectionConfiguration.createFrom(contentPage, generator)

    Seq(
      TimelinePage(compiledData, configuration)
    )

  }

}
