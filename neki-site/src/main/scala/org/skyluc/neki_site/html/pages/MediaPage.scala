package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Media
import org.skyluc.fan_resources.data.SummaryItem
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*
import org.skyluc.fan_resources.html.CompiledDataGenerator

class MediaPage(
    media: Media,
    mediaCompiledData: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  import MediaPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(mediaCompiledData)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, mediaCompiledData.uId)

    val summarySection = summaryContent()

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, mediaCompiledData.uId)

    Seq(
      largeDetails
    ) ++ multiMediaMainSections
      ++ summarySection
      ++ additionalSection
  }

  private def summaryContent(): List[BodyElement[?]] = {
    media.summary
      .map { summary =>
        List(
          SectionHeader.generateWithStatus("Summary", summary.status.description, summary.status.code),
          ul()
            .withClass(CLASS_MEDIA_SUMMARY_BLOCK)
            .appendElements(
              summary.items.map(generate)*
            ),
        )
      }
      .getOrElse(Nil)
  }

  private def generate(summaryItem: SummaryItem): Li = {
    val elements: List[BodyElement[?]] = List(
      Some(text(summaryItem.label)),
      if (summaryItem.sub.isEmpty) {
        None
      } else {
        Some(
          ul().appendElements(
            summaryItem.sub.map(generate)*
          )
        )
      },
    ).flatten
    li().appendElements(
      elements*
    )
  }

}

object MediaPage {

  // classes

  val CLASS_MEDIA_SUMMARY_BLOCK = "media-summary-block"

  // info

  val LABEL_HOST = "host"
  val VALUE_SHOW_PAGE = "show page"
  val VALUE_PROGRAM = "program"
  val VALUE_PUBLICATION_PAGE = "publication page"
  val VALUE_ARTICLE = "article"

  def pageFor(media: Media, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {
    val multimediaBlock = generator.getMultiMediaBlock(media)

    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(media.id.path.insertSecond(Common.EXTRA))
    }

    val compiledData = generator.getElement(media)

    val mainPage = MediaPage(
      media,
      compiledData,
      multimediaBlock,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          media.title(),
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          media.title(),
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(media.id.path),
        media.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
      ),
      site,
    )
    extraPath
      .map { extraPath =>
        val extraPage = MediaExtraPage(
          compiledData,
          multimediaBlock,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              media.title(),
              None,
              None,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              media.title(),
              None,
              None,
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
