package org.skyluc.neki_site.html.pages

import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Event
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.LineCard
import org.skyluc.fan_resources.html.component.MainTitle
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.TitleAndDescription

class EventPage(
    eventCompiledData: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails = LargeDetails.generate(eventCompiledData)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, eventCompiledData.uId)

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, eventCompiledData.uId)

    Seq(
      largeDetails
    ) ++ multiMediaMainSections
      ++ additionalSection
  }

}

class EventExtraPage(
    eventCompiledData: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val mediaSection =
      MultiMediaCard.generateExtraMediaSection(
        multimediaBlock,
        eventCompiledData.uId,
      )

    Seq(
      MainTitle.generate(
        LineCard.generate(eventCompiledData)
      )
    )
      ++ mediaSection

  }
}

object EventPage {

  def pagesFor(event: Event, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {
    val compiledData = generator.getElement(event)
    val multimediaBlock = generator.getMultiMediaBlock(event)

    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(event.id.path.insertSecond(Common.EXTRA))
    }

    val mainPage = EventPage(
      compiledData,
      multimediaBlock,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          compiledData.label,
          None,
          compiledData.shortLabel,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          compiledData.label,
          None,
          compiledData.shortLabel,
          None,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(event.id.path),
        event.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
      ),
      site,
    )

    extraPath
      .map { extraPath =>
        val extraPage = EventExtraPage(
          compiledData,
          multimediaBlock,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              compiledData.label,
              None,
              compiledData.shortLabel,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              compiledData.label,
              None,
              compiledData.shortLabel,
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
