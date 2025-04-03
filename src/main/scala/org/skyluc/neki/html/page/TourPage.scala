package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.Tour

class TourPage(val tour: Tour, data: Data) extends Page(data) {

  import TourPage._

  override def path(): Path = Path.of(SHOW_PATH, tour.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    tour.shortname.getOrElse(tour.fullname) + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = tour.shortname.map(_ => tour.fullname)

  override def mainContent(): List[BodyElement[?]] = {
    List(
      ItemDetails.generate(CompiledData.getTour(tour.id, data)),
      SectionHeader.generate(SECTION_SONGS_TEXT),
      MediumCard.generateList(tour.shows.map(CompiledData.getShow(_, data))),
    )
  }

}

object TourPage {
  val SHOW_PATH = "tour"

  val DESIGNATION = "Tour"
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val LABEL_DATES = "dates"
  val VALUE_EVENT_PAGE = "event page" // TODO: used in show too. extract

  val SECTION_SONGS_TEXT = "Shows"
}
