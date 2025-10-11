package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.BodyElement

class CategoriesPage(
    categoriesPage: fr.compileddata.ElementCompiledData,
    configuration: fr.component.ChronologySectionConfiguration,
) extends MainSitePage {

  override val outputPath: dfr.Path = categoriesPage.id.path.tailSegments().withExtension(Common.HTML_EXTENSION)

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle(categoriesPage.label),
      TitleGenerator.generateDescription(categoriesPage.label),
      outputPath.toAbsoluteString(),
      categoriesPage.cover.imageUrl,
    )

  override def mainContent(): Seq[BodyElement[?]] = {
    categoriesPage.description.map { d =>
      fr.component.MainIntro.generate(d)
    }.toSeq
      :+ fr.component.ChronologySection.generate(configuration)
  }
}

object CategoriesPage {
  def pagesFor(
      categoriesPage: dfr.CategoriesPage,
      generator: fr.compileddata.CompiledDataGenerator,
  ): Seq[MainSitePage] = {
    val compiledData = generator.getElementCompiledData(categoriesPage)

    val configuration = fr.component.ChronologySectionConfiguration.createFromIdPrefixes(
      categoriesPage.idPrefix,
      categoriesPage.startDate,
      categoriesPage.endDate,
      categoriesPage.categories,
      categoriesPage.displayType,
      generator,
    )

    Seq(
      CategoriesPage(compiledData, configuration)
    )
  }
}
