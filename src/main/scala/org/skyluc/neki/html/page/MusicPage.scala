package org.skyluc.neki.html.page

import org.skyluc.neki.data.{MusicPage as dMusicPage, Data}
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.html.Html._
import org.skyluc.neki.html.Pages
import org.skyluc.neki.data.AlbumId
import org.skyluc.neki.data.SongId
import org.skyluc.collection.LayeredNode
import org.skyluc.neki.html.CompiledData
import org.skyluc.collection.LayeredData
import org.skyluc.neki.html.MediumCard
import org.skyluc.neki.html.ItemCompiledData
import org.skyluc.neki.html.MainIntro

class MusicPage(val page: dMusicPage, data: Data) extends Page(data) {
  import MusicPage._

  override def path(): Path = Path.of(MUSIC_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val tree: LayeredData[ItemCompiledData] = page.music.map {
      case a: AlbumId =>
        LayeredNode(
          CompiledData.getAlbum(a, data),
          data.albums(a).songs.map { s => LayeredNode(CompiledData.getSong(s, data)) },
        )
      case s: SongId =>
        LayeredNode(CompiledData.getSong(s, data))
    }

    List(
      MainIntro.generate(MAIN_INTRO_TEXT),
      MediumCard.generateTree(tree),
    )
  }

}

object MusicPage {
  val MUSIC_PATH = "music" + Pages.HTML_EXTENSION
  val DESIGNATION = "Music"

  val MAIN_INTRO_TEXT = "The albums and songs released by NEK!."
}
