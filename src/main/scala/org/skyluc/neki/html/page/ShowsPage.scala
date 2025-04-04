package org.skyluc.neki.html.page

import org.skyluc.neki.data.{ShowsPage as dShowsPage, Data}
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.html.Html._
import org.skyluc.neki.html.Pages
import org.skyluc.collection.LayeredNode
import org.skyluc.neki.html.CompiledData
import org.skyluc.collection.LayeredData
import org.skyluc.neki.html.MediumCard
import org.skyluc.neki.html.ItemCompiledData
import org.skyluc.neki.html.MainIntro
import org.skyluc.neki.data.ShowId
import org.skyluc.neki.data.TourId

class ShowsPage(val page: dShowsPage, data: Data) extends Page(data) {
  import ShowsPage._

  override def path(): Path = Path.of(SHOWS_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val tree: LayeredData[ItemCompiledData] = page.shows.map {
      case s: ShowId =>
        LayeredNode(CompiledData.getShow(s, data))
      case t: TourId =>
        LayeredNode(
          CompiledData.getTour(t, data),
          data.tours(t).shows.map { s => LayeredNode(CompiledData.getShow(s, data)) },
        )
    }

    List(
      MainIntro.generate(MAIN_INTRO_CONTENT),
      MediumCard.generateTree(tree),
    )

  }

}

object ShowsPage {
  val SHOWS_PATH = "shows" + Pages.HTML_EXTENSION
  val DESIGNATION = "Shows"

  val MAIN_INTRO_CONTENT: List[BodyElement[?]] = List(
    text("The main shows performed by NEK!. To see all the shows where NEK! has performed, check the "),
    // TODO: use the page id info ?
    a()
      .withHref("/chronology.html")
      .appendElements(
        text("Chronology page")
      ),
    text("."),
  )
}
