package org.skyluc.neki.html.page

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement

class SongPage(val song: Song, data: Data) extends Page(data) {

  import SongPage._

  override def path(): Path = Path.of(SONG_PATH, song.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    song.fullname + song.fullnameEn.map(n => s" ($n)").getOrElse(CommonBase.EMPTY) + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val videoSection = MultiMediaCard.generateSection(
      SECTION_VIDEO_TEXT,
      CompiledData.getMultiMedia(song.multimedia.video, data),
      Song.FROM_KEY,
    )

    val liveSection = MultiMediaCard.generateSection(
      SECTION_LIVE_TEXT,
      CompiledData.getMultiMedia(song.multimedia.live, data),
      Song.FROM_KEY,
    )

    val additionalSection = MultiMediaCard.generateSection(
      SECTION_ADDITIONAL_TEXT,
      CompiledData.getMultiMedia(song.multimedia.additional, data),
      Song.FROM_KEY,
    )

    List(
      ItemDetails.generate(CompiledData.getSong(song.id, data))
    ) ::: videoSection ::: liveSection ::: additionalSection
  }

}

object SongPage {
  val SONG_PATH = "song"

  val DESIGNATION = "Song"
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val LABEL_LYRICIST = "lyricist"
  val LABEL_COMPOSER = "composer"

  val SECTION_VIDEO_TEXT = "Video"
  val SECTION_LIVE_TEXT = "Live"
  val SECTION_ADDITIONAL_TEXT = "Additional Media"
}
