package org.skyluc.neki.html.page

import org.skyluc.neki.data.Media
import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.ItemDetails
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.Pages
import org.skyluc.neki.html.CoverImage
import org.skyluc.neki.html.SectionHeader
import org.skyluc.neki.data.SummaryItem
import org.skyluc.html._
import Html._
import org.skyluc.neki.html.MultiMediaCard

class MediaPage(val media: Media, data: Data) extends Page(data) {
  import MediaPage._

  override def path(): Path = Path.of(MEDIA_PATH, media.id.year, media.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = media.title() + TITLE_DESIGNATION

  override def altName(): Option[String] = None

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(media.coverImage, media, data))

  override def mainContent(): List[BodyElement[?]] = {

    val mainSections = MultiMediaCard.generateMainSections(
      media.multimedia,
      data,
      Media.FROM_KEY,
    )

    ItemDetails.generate(CompiledData.getMedia(media.id, data))
      :: mainSections ::: summaryContent()
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
  val MEDIA_PATH = "media"

  val DESIGNATION = "Interview" // TODO: will need subtyping at some point
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val LABEL_HOST = "host"
  val VALUE_SHOW_PAGE = "show page"
  val VALUE_PROGRAM = "program"
  val VALUE_PUBLICATION_PAGE = "publication page"
  val VALUE_ARTICLE = "article"

  val CLASS_MEDIA_SUMMARY_BLOCK = "media-summary-block"

}
