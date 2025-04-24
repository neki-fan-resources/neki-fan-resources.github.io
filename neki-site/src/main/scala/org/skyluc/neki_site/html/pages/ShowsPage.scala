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
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*
import org.skyluc.neki_site.html.Site

class ShowsPage(showsPage: d.ShowsPage, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  import ShowsPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    val tree: LayeredData[ElementCompiledData] = showsPage.shows.map {
      case s: ShowId =>
        LayeredNode(compilers.elementDataCompiler.get(s))
      case t: TourId =>
        LayeredNode(
          compilers.elementDataCompiler.get(t),
          compilers.data.get(t).shows.map { s => LayeredNode(compilers.elementDataCompiler.get(s)) },
        )
    }

    List(
      MainIntro.generate(MAIN_INTRO_CONTENT),
      MediumCard.generateHybridTree(tree),
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

  def pagesFor(showsPage: d.ShowsPage, compilers: Compilers): Seq[SitePage] = {
    val mainPage = ShowsPage(
      showsPage,
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
      compilers,
    )

    Seq(mainPage)
  }
}
