package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.*
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.fan_resources.html.CompiledDataGenerator

class ShowPage(
    show: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(show)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, Show.FROM_KEY)

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, Show.FROM_KEY)

    Seq(
      largeDetails
    ) ++ multiMediaMainSections
      ++ additionalSection
  }
}

object ShowPage {
  val DESIGNATION = "show"

  val URL_SETLISTFM_BASE = "https://www.setlist.fm/setlist/"

  val LABEL_VENUE = "venue"
  val LABEL_SETLIST = "setlist"
  val VALUE_SETLIST = "setlist.fm"
  val VALUE_EVENT_PAGE = "event page"

  def pagesFor(show: Show, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val compiledData = generator.getElement(show)
    val multimediaBlock = generator.getMultiMediaBlock(show)

    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(show.id.path.insertSecond(Common.EXTRA))
    }

    val mainPage = ShowPage(
      compiledData,
      multimediaBlock,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          show.fullname,
          None,
          show.shortname,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          show.fullname,
          None,
          show.shortname,
          None,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(show.id.path),
        show.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
      ),
      site,
    )

    extraPath
      .map { extraPath =>
        val extraPage = ShowExtraPage(
          compiledData,
          multimediaBlock,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              show.fullname,
              None,
              show.shortname,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              show.fullname,
              None,
              show.shortname,
              None,
            ),
            SitePage.absoluteUrl(compiledData.cover.source),
            SitePage.canonicalUrlFor(extraPath),
            extraPath.withExtension(Common.HTML_EXTENSION),
            None,
            None,
            false,
          ),
          site,
        )
        Seq(extraPage, mainPage)
      }
      .getOrElse(
        Seq(
          mainPage
        )
      )
  }
}
