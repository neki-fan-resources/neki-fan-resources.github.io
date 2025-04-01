package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html._
import Html._
import java.nio.file.Path
import org.skyluc.neki.html.Pages
import org.skyluc.neki.html.MainIntro
import org.skyluc.neki.html.SectionHeader
import org.skyluc.neki.html.LineCard
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.neki.data.Date

class LivePage(data: Data) extends Page(data) {

  import LivePage._

  override def path(): Path = Path.of(LIVE_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {

    val withLiveVideo = data.songs.values.filter(!_.multimedia.live.isEmpty).toList

    // the song with most recent video first
    val orderedWithLiveVideo = withLiveVideo.sortBy { song =>
      song.multimedia.live.map(data.multimedia(_).publishedDate).max
    }.reverse

    val songSections: List[BodyElement[?]] = orderedWithLiveVideo.flatMap { song =>
      List(
        SectionHeader.generate(LineCard.generate(CompiledData.getSong(song.id, data))),
        MultiMediaCard.generateList(CompiledData.getMultiMedia(song.multimedia.live, data)),
      )
    }.toList

    List(
      MainIntro.generate(MAIN_INTRO_TEXT)
    ) ::: songSections
  }

}

object LivePage {
  val LIVE_PATH = "live" + Pages.HTML_EXTENSION
  val DESIGNATION = "Live"

  val MAIN_INTRO_TEXT = "Videos of live performances by NEK!."
}
