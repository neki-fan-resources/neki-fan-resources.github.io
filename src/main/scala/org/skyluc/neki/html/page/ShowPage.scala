package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.html.Html
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.Show

class ShowPage(val show: Show, data: Data) extends Page(data) {

  import ShowPage._

  override def path(): Path = Path.of(SHOW_PATH, show.id.year, show.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    show.fullname + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = show.shortname

  override def mainContent(): List[BodyElement[?]] = {
    List(
      ItemDetails.generate(CompiledData.getShow(show.id, data))
    )
  }

}

object ShowPage {
  val SHOW_PATH = "show"

  val DESIGNATION = "Show"
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val URL_SETLISTFM_BASE = "https://www.setlist.fm/setlist/"
  val LABEL_VENUE = "venue"
  val LABEL_SETLIST = "setlist"
  val VALUE_SETLIST = "setlist.fm"
  val VALUE_EVENT_PAGE = "event page"
}
