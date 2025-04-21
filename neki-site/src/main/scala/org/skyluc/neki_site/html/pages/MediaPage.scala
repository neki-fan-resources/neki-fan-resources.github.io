package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.data.Media
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html._
import Html._
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.fan_resources.data.SummaryItem
import org.skyluc.neki_site.html.TitleAndDescription

class MediaPage(media: Media, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  import MediaPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(compilers.elementDataCompiler.get(media))

    val multimediaBlock = compilers.multimediaDataCompiler.get(media.multimedia, media.linkedTo)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, Media.FROM_KEY)

    val summarySection = summaryContent()

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, Media.FROM_KEY)

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

  def pageFor(media: Media, compilers: Compilers): Seq[SitePage] = {
    val compiledData = compilers.elementDataCompiler.get(media)

    val mainPage = MediaPage(
      media,
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
        compiledData.cover.source,
        SitePage.canonicalUrlFor(media.id.path),
        media.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      compilers,
    )

    Seq(mainPage)
  }
}
