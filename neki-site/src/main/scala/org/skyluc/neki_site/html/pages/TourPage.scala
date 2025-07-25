package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Tour
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.component.ChronologySection
import org.skyluc.fan_resources.html.component.ChronologySection.ChronologyYear
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class TourPage(
    tour: ElementCompiledData,
    shows: Seq[ChronologyYear],
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  import TourPage._

  override def javascriptFiles(): Seq[Url] =
    super.javascriptFiles()
      :+ Url(SitePage.SRC_OVERLAY_JAVASCRIPT)
      :+ Url(SitePage.SRC_FRMAIN_JAVASCRIPT)
      :+ Url(SitePage.SRC_CONTENT_JAVASCRIPT)

  override def elementContent(): Seq[BodyElement[?]] = {

    val largeDetails =
      LargeDetails.generate(tour)

    val showsSection: Seq[BodyElement[?]] = Seq(
      SectionHeader.generate(SECTION_SHOWS),
      ChronologySection.generate(shows, true, false),
    )

    Seq(
      largeDetails
    ) ++ showsSection
      ++ MultiMediaCard.generateMainSections(multimediaBlock, Tour.FROM_KEY)
  }
}

object TourPage {
  val DESIGNATION = "tour"

  val SECTION_SHOWS = "Shows"

  val LABEL_DATES = "dates"
  val VALUE_EVENT_PAGE = "event page" // TODO: used in show too. extract

  // -------------

  def pagesFor(tour: Tour, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val compiledData = generator.getElement(tour)
    val multimediaBlock = generator.getMultiMediaBlock(tour)

    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(tour.id.path.insertSecond(Common.EXTRA))
    }

    val shows = ChronologySection.compiledDataIds(
      tour.firstDate,
      tour.lastDate,
      tour.shows,
      generator,
    )

    val mainPage = TourPage(
      compiledData,
      shows,
      multimediaBlock,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          tour.fullname,
          None,
          tour.shortname,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          tour.fullname,
          None,
          tour.shortname,
          None,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(tour.id.path),
        tour.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
        false,
      ),
      site,
    )

    extraPath
      .map { extraPath =>
        val extraPage = TourExtraPage(
          compiledData,
          multimediaBlock,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              tour.fullname,
              None,
              tour.shortname,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              tour.fullname,
              None,
              tour.shortname,
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
