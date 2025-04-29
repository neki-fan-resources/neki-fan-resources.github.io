package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Tour
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class TourPage(tour: ElementCompiledData, shows: Seq[ElementCompiledData], description: PageDescription, site: Site)
    extends SitePage(description, site) {

  import TourPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    val largeDetails =
      LargeDetails.generate(tour)

    val showsSection: Seq[BodyElement[?]] = Seq(
      SectionHeader.generate(SECTION_SHOWS),
      MediumCard.generateList(shows),
    )

    Seq(
      largeDetails
    ) ++ showsSection
  }
}

object TourPage {
  val DESIGNATION = "tour"

  val DATE_RANGE_SEPARATOR = " - "

  val SECTION_SHOWS = "Shows"

  val LABEL_DATES = "dates"
  val VALUE_EVENT_PAGE = "event page" // TODO: used in show too. extract

  // -------------

  def pagesFor(tour: Tour, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val compiledData = generator.getElement(tour)

    val shows =
      tour.shows.map(generator.getElement(_))

    Seq(
      TourPage(
        compiledData,
        shows,
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
          None,
          false,
        ),
        site,
      )
    )
  }

}
