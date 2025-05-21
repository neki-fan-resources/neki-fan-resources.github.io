package org.skyluc.neki_site.html.pages

import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.data.ShowId
import org.skyluc.fan_resources.data.TourId
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.html.*
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*
import org.skyluc.fan_resources.html.CompiledDataGenerator

class ShowsPage(shows: LayeredData[ElementCompiledData], description: PageDescription, site: d.Site)
    extends SitePage(description, site) {

  import ShowsPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    List(
      MainIntro.generate(MAIN_INTRO_CONTENT*),
      MediumCard.generateHybridTree(shows),
    )

  }
}

object ShowsPage {

  val PAGE_PATH = Path("shows")

  val MAIN_INTRO_CONTENT: List[BodyElement[?]] = List(
    text("The main shows performed by NEK!. To see all the shows where NEK! has performed, check the "),
    // TODO: use the page id info ?
    a()
      .withHref("/chronology.html")
      .appendElements(
        text("Chronology page")
      ),
    text("."),
  )

  def pagesFor(showsPage: d.ShowsPage, site: d.Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val shows: LayeredData[ElementCompiledData] = showsPage.shows.map {
      case s: ShowId =>
        LayeredNode(generator.getElement(s))
      case t: TourId =>
        LayeredNode(
          generator.getElement(t),
          generator.get(t).shows.map { s => LayeredNode(generator.getElement(s)) },
        )
    }

    val mainPage = ShowsPage(
      shows,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Shows",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Shows",
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(Site.DEFAULT_COVER_IMAGE.source),
        SitePage.canonicalUrlFor(PAGE_PATH),
        PAGE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      site,
    )

    Seq(mainPage)
  }
}
