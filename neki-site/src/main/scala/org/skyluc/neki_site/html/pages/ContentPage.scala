package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.component.ChronologySection
import org.skyluc.fan_resources.html.component.ChronologySection.*
import org.skyluc.fan_resources.html.component.CompiledDataJavascript
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.neki_site.html.component.TimelineBlock

import dfr.DisplayMetadata
import dfr.CategoryDescriptor

class ContentPage(
    description: String,
    years: Seq[ChronologyYear],
    categories: Seq[CategoryDescriptor],
    withLinks: Boolean,
    withSubElements: Boolean,
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
      ChronologySection.generate(years, categories, withLinks, withSubElements),
    )
  }

}

class TimelinePage(
    description: String,
    startDate: dfr.Date,
    endDate: dfr.Date,
    elements: Seq[ElementCompiledData],
    metadata: Map[String, DisplayMetadata],
    pageDescription: PageDescription,
    site: Site,
) extends SitePage(pageDescription, site) {

  override def javascriptFiles(): Seq[Url] =
    super.javascriptFiles()
      :+ Url(SitePage.SRC_FRMAIN_JAVASCRIPT)
      :+ Url(SitePage.SRC_OVERLAY_JAVASCRIPT)

  override def elementContent(): Seq[BodyElement[?]] = {
    Seq(
      MainIntro.generate(description),
      TimelineBlock.generate(startDate, endDate, elements, metadata),
      CompiledDataJavascript.generateOverlayContent(elements),
    )
  }
}

object ContentPage {

  def pageFor(contentPage: dfr.ContentPage, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val (pagePath, oppositePagePath) =
      SitePage.pageAndOppositePagePath(
        contentPage.id.path.tailSegments(),
        contentPage.id,
        contentPage.id.copy(dark = !contentPage.id.dark),
        contentPage.id.dark,
        generator,
      )

    val pageDescription =
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          contentPage.label,
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          contentPage.label,
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(generator.getMultiMedia(contentPage.coverImage).image.source),
        SitePage.canonicalUrlFor(pagePath),
        pagePath.withExtension(Common.HTML_EXTENSION),
        oppositePagePath.map(SitePage.urlFor(_)),
        None,
        contentPage.id.dark,
      )

    if (contentPage.displayType == "timeline") {
      val compiledData = contentPage.content.map(generator.getElement)
      Seq(
        TimelinePage(
          contentPage.description,
          contentPage.startDate,
          contentPage.endDate,
          compiledData,
          contentPage.metadata.map(t => (t._1.uId(), t._2)),
          pageDescription,
          site,
        )
      )

    } else {
      val byYears = ChronologySection.compiledDataIds(
        contentPage.startDate,
        contentPage.endDate,
        contentPage.content,
        generator,
      )

      val (withLinks, withSubElements) = if (contentPage.displayType == "content") {
        (true, true)
      } else if (contentPage.displayType == "choronology") {
        (false, false)
      } else {
        (false, true)
      }

      val mainPage =
        ContentPage(
          contentPage.description,
          byYears,
          Nil,
          withLinks,
          withSubElements,
          pageDescription,
          site,
        )

      Seq(mainPage)
    }
  }

}
