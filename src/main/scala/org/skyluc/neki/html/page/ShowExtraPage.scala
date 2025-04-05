package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.data.Show
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.LineCard
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.html.Html._
import org.skyluc.neki.html.CommonBase

class ShowExtraPage(show: Show, data: Data) extends Page(data) {

  import ShowPage._

  override def path(): Path = extraPath(show)

  override def shortTitle(): String = {
    show.fullname + TITLE_DESIGNATION_EXTRA
  }

  override def altName(): Option[String] = show.shortname

  override def mainContent(): List[BodyElement[?]] = {
    val em = extraMultimedia(show)
    val mediaSection = if (em.isEmpty) {
      Nil
    } else {
      val sorted = em.map(CompiledData.getMultiMedia(_, data)).sortBy(_.date).reverse
      MultiMediaCard.generateSection(
        "Media",
        sorted,
        Show.FROM_KEY,
      )
    }
    div()
      .withClass(CommonBase.CLASS_MAIN_TITLE)
      .appendElements(LineCard.generate(CompiledData.getShow(show.id, data))) :: mediaSection
  }

}
