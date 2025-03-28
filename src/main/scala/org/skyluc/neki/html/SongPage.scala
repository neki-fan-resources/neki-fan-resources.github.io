package org.skyluc.neki.html

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.html.Html

case class SongPage(song: Song) extends Page {

  import SongPage._

  override def getPath(): Path = Path.of(SONG_PATH, song.id.id + Pages.HTML_EXTENSION)

  override def getContent(): Html = {
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