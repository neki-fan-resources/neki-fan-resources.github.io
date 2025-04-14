package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.Show
import org.skyluc.neki.html.CommonBase.COMMON_TITLE_LIMIT

class ShowPage(val show: Show, extraPage: Boolean, data: Data) extends Page(data) {

  import ShowPage._

  override def path(): Path = Path.of(SHOW_PATH, show.id.year, show.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    if (show.fullname.length() <= TITLE_LIMIT) {
      show.fullname + TITLE_DESIGNATION
    } else {
      show.shortname
        .map { n =>
          n.takeRight(TITLE_LIMIT) + TITLE_DESIGNATION
        }
        .getOrElse {
          show.fullname.takeRight(TITLE_LIMIT) + TITLE_DESIGNATION
        }
    }
  }

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(show.coverImage, show, data))

  override def altName(): Option[String] = show.shortname

  override def mainContent(): List[BodyElement[?]] = {

    val mainSections = MultiMediaCard.generateMainSections(show.multimedia, data, Show.FROM_KEY)

    val additionalSection = MultiMediaCard.generateAdditionalSection(show.multimedia, data, Show.FROM_KEY)

    val extraSection = if (extraPage) {
      List(ExtraLink.generate("/" + extraPath(show).toString()))
    } else {
      Nil
    }

    List(
      ItemDetails.generate(CompiledData.getShow(show.id, data))
    ) ::: mainSections
      ::: additionalSection
      ::: extraSection
  }

}

object ShowPage {
  val SHOW_PATH = "show"

  val DESIGNATION = "Show"
  val TITLE_DESIGNATION = " - " + DESIGNATION
  val TITLE_LIMIT = COMMON_TITLE_LIMIT - TITLE_DESIGNATION.length()
  val TITLE_DESIGNATION_EXTRA = " - " + DESIGNATION + " extra"
  val TITLE_EXTRA_LIMIT = COMMON_TITLE_LIMIT - TITLE_DESIGNATION_EXTRA.length()

  val URL_SETLISTFM_BASE = "https://www.setlist.fm/setlist/"
  val LABEL_VENUE = "venue"
  val LABEL_SETLIST = "setlist"
  val VALUE_SETLIST = "setlist.fm"
  val VALUE_EVENT_PAGE = "event page"

  def extraPath(show: Show): Path =
    Path.of(SHOW_PATH, Pages.EXTRA_PATH, show.id.year, show.id.id + Pages.HTML_EXTENSION)

  def pagesFor(show: Show, data: Data): List[Page] = {
    if (show.multimedia.extra(show.relatedTo).isEmpty) {
      List(ShowPage(show, false, data))
    } else {
      List(ShowPage(show, true, data), ShowExtraPage(show, data))
    }
  }
}
