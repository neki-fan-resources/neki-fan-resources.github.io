package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.neki.html.Pages
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.MainIntro
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MediumCard

class MediasPage(data: Data) extends Page(data) {
  import MediasPage._

  override def path(): Path = Path.of(MEDIAS_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val list = data.medias.values
      .map { m =>
        CompiledData.getMedia(m.id, data)
      }
      .toList
      .sortBy(_.date)
      .reverse
    List(MainIntro.generate(MAIN_INTRO_CONTENT), MediumCard.generateList(list))
  }

}

object MediasPage {
  val MEDIAS_PATH = "medias" + Pages.HTML_EXTENSION
  val DESIGNATION = "Medias"

  val MAIN_INTRO_CONTENT = "The media and press events NEK! was part of."
}
