package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.Show
import org.skyluc.neki.data.MultiMediaId

class ShowPage(val show: Show, extraPage: Boolean, data: Data) extends Page(data) {

  import ShowPage._

  override def path(): Path = Path.of(SHOW_PATH, show.id.year, show.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    show.fullname + TITLE_DESIGNATION
  }

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(show.coverImage, show, data))

  override def altName(): Option[String] = show.shortname

  override def mainContent(): List[BodyElement[?]] = {

    val concertSection: List[BodyElement[?]] = MultiMediaCard.generateSection(
      SECTION_CONCERT_TEXT,
      CompiledData.getMultiMedia(show.multimedia.concert, data),
      Show.FROM_KEY,
    )

    val videoSection: List[BodyElement[?]] = MultiMediaCard.generateSection(
      SECTION_VIDEO_TEXT,
      CompiledData.getMultiMedia(show.multimedia.video, data),
      Show.FROM_KEY,
    )

    val shortSection: List[BodyElement[?]] = MultiMediaCard.generateSection(
      SECTION_SHORT_TEXT,
      CompiledData.getMultiMedia(show.multimedia.short, data),
      Show.FROM_KEY,
    )

    val additionalSection: List[BodyElement[?]] = MultiMediaCard.generateSection(
      SECTION_ADDITIONAL_TEXT,
      CompiledData.getMultiMedia(show.multimedia.additional, data),
      Show.FROM_KEY,
    )

    val extraSection = if (extraPage) {
      List(ExtraLink.generate("/" + extraPath(show).toString()))
    } else {
      Nil
    }

    List(
      ItemDetails.generate(CompiledData.getShow(show.id, data))
    ) ::: concertSection
      ::: videoSection
      ::: shortSection
      ::: additionalSection
      ::: extraSection
  }

}

object ShowPage {
  val SHOW_PATH = "show"

  val DESIGNATION = "Show"
  val TITLE_DESIGNATION = " - " + DESIGNATION
  val TITLE_DESIGNATION_EXTRA = " - " + DESIGNATION + " extra"

  val URL_SETLISTFM_BASE = "https://www.setlist.fm/setlist/"
  val LABEL_VENUE = "venue"
  val LABEL_SETLIST = "setlist"
  val VALUE_SETLIST = "setlist.fm"
  val VALUE_EVENT_PAGE = "event page"

  val SECTION_CONCERT_TEXT = "Concert"
  val SECTION_VIDEO_TEXT = "Video"
  val SECTION_SHORT_TEXT = "Short"
  val SECTION_ADDITIONAL_TEXT = "Additional"

  def extraPath(show: Show): Path =
    Path.of(SHOW_PATH, Pages.EXTRA_PATH, show.id.year, show.id.id + Pages.HTML_EXTENSION)

  def extraMultimedia(show: Show): List[MultiMediaId] = {
    val allMultiMediaInMainPage = show.multimedia.all()
    val allRelatedMultiMedia: List[MultiMediaId] = show.relatedTo.flatMap {
      case m: MultiMediaId =>
        Some(m)
      case _ =>
        None
    }
    allRelatedMultiMedia.filterNot(allMultiMediaInMainPage.contains(_))
  }

  def pagesFor(show: Show, data: Data): List[Page] = {
    if (extraMultimedia(show).isEmpty) {
      List(ShowPage(show, false, data))
    } else {
      List(ShowPage(show, true, data), ShowExtraPage(show, data))
    }
  }
}
