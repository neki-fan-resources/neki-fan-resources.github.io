package org.skyluc.neki.html.page

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.html.Html
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement

class SongPage(val song: Song, data: Data) extends Page(data) {

  import SongPage._

  val compiledData: ItemCompiledData = {
    val info =
      ItemInfo(CompiledData.LABEL_RELEASED, song.releaseDate.toString()) ::
        song.credits
          .map { credits =>
            List(
              ItemInfo(LABEL_LYRICIST, credits.lyricist),
              ItemInfo(LABEL_COMPOSER, credits.composer),
            )
          }
          .getOrElse(Nil)
    ItemCompiledData(
      DESIGNATION,
      song.fullname,
      song.fullnameEn,
      "", // TODO
      "", // TODO
      info,
    )
  }

  override def path(): Path = Path.of(SONG_PATH, song.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    song.fullname + song.fullnameEn.map(n => s" ($n)").getOrElse(CommonBase.EMPTY) + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    List(
      ItemDetails.generate(compiledData)
    )
  }

}

object SongPage {
  val SONG_PATH = "song"

  val DESIGNATION = "Song"
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val LABEL_LYRICIST = "lyricist"
  val LABEL_COMPOSER = "composer"
}
