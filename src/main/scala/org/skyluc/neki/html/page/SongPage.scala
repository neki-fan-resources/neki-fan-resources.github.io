package org.skyluc.neki.html.page

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.html.Html
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._


class SongPage(val song: Song, data: Data) extends Page(data) {

  import SongPage._

  override def path(): Path = Path.of(SONG_PATH, song.id.id + Pages.HTML_EXTENSION)

  override def content(): Html = {
    CommonBase.generate(this)
  }

  override def shortTitle(): String = {
    song.fullname + song.fullnameEn.map(n => s" ($n)").getOrElse(CommonBase.EMPTY) + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = None

}

object SongPage {
  final val SONG_PATH = "song"

  final val TITLE_DESIGNATION = " - Song"
}