package org.skyluc.neki.html.page

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.MultiMediaId

class SongPage(val song: Song, extraPage: Boolean, data: Data) extends Page(data) {

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

    val shortSection = MultiMediaCard.generateSection(
      SECTION_SHORT_TEXT,
      CompiledData.getMultiMedia(song.multimedia.short, data),
      Song.FROM_KEY,
    )

    val additionalSection = MultiMediaCard.generateSection(
      SECTION_ADDITIONAL_TEXT,
      CompiledData.getMultiMedia(song.multimedia.additional, data),
      Song.FROM_KEY,
    )

    val extraSection = if (extraPage) {
      List(ExtraLink.generate("/" + extraPath(song).toString()))
    } else {
      Nil
    }

    List(
      ItemDetails.generate(CompiledData.getSong(song.id, data))
    ) ::: videoSection ::: liveSection ::: shortSection ::: additionalSection ::: extraSection
  }

}

object SongPage {
  val SONG_PATH = "song"

  val DESIGNATION = "Song"
  val TITLE_DESIGNATION = " - " + DESIGNATION
  val TITLE_DESIGNATION_EXTRA = " - " + DESIGNATION + " extra"

  val LABEL_LYRICIST = "lyricist"
  val LABEL_COMPOSER = "composer"

  val SECTION_VIDEO_TEXT = "Video"
  val SECTION_LIVE_TEXT = "Live"
  val SECTION_SHORT_TEXT = "Short"
  val SECTION_ADDITIONAL_TEXT = "Additional Media"

  def extraPath(song: Song): Path = Path.of(SONG_PATH, Pages.EXTRA_PATH, song.id.id + Pages.HTML_EXTENSION)

  def extraMultimedia(song: Song): List[MultiMediaId] = {
    val allMultiMediaInMainPage = song.multimedia.all()
    val allRelatedMultiMedia: List[MultiMediaId] = song.relatedTo.flatMap {
      case m: MultiMediaId =>
        Some(m)
      case _ =>
        None
    }
    allRelatedMultiMedia.filterNot(allMultiMediaInMainPage.contains(_))
  }

  def pagesFor(song: Song, data: Data): List[Page] = {
    if (extraMultimedia(song).isEmpty) {
      List(SongPage(song, false, data))
    } else {
      List(SongPage(song, true, data), SongExtraPage(song, data))
    }
  }
}
