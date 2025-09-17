package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.component.ChronologySection
import org.skyluc.fan_resources.html.component.ChronologySection.*
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import dfr.CategoryDescriptor

class CategoriesPage(
    description: String,
    years: Seq[ChronologyYear],
    categories: Seq[CategoryDescriptor],
    pageDescription: PageDescription,
    site: Site,
) extends SitePage(pageDescription, site) {

  override def javascriptFiles(): Seq[Url] =
    super.javascriptFiles()
      :+ Url(SitePage.SRC_FRMAIN_JAVASCRIPT)
      :+ Url(SitePage.SRC_OVERLAY_JAVASCRIPT)
      :+ Url(SitePage.SRC_CONTENT_JAVASCRIPT)

  override def elementContent(): Seq[BodyElement[?]] = {
    Seq(
      MainIntro.generate(description),
      ChronologySection.generate(years, categories, true, false),
    )
  }

}

object CategoriesPage {

  def pageFor(categoriesPage: dfr.CategoriesPage, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val byYears = ChronologySection.compiledDataCategories(
      categoriesPage.startDate,
      categoriesPage.endDate,
      categoriesPage.idPrefix,
      generator,
    )

    val path = Path(categoriesPage.id.id)

    val mainPage =
      CategoriesPage(
        categoriesPage.description,
        byYears,
        categoriesPage.categories,
        PageDescription(
          TitleAndDescription.formattedTitle(
            None,
            None,
            categoriesPage.label,
            None,
            None,
            None,
          ),
          TitleAndDescription.formattedDescription(
            None,
            None,
            categoriesPage.label,
            None,
            None,
            None,
          ),
          SitePage.absoluteUrl(generator.getMultiMedia(categoriesPage.coverImage).image.source),
          SitePage.canonicalUrlFor(path),
          path.withExtension(Common.HTML_EXTENSION),
          None,
          None,
          categoriesPage.id.dark,
        ),
        site,
      )

    Seq(mainPage)
  }

}
