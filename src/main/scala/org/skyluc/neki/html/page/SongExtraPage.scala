package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.data.Song
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.CommonBase
import org.skyluc.neki.html.LineCard
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.html.Html._
import org.skyluc.neki.html.CoverImage

class SongExtraPage(song: Song, data: Data) extends Page(data) {

  import SongPage._

  override def path(): Path = extraPath(song)

  override def shortTitle(): String = {
    song.fullname + song.fullnameEn.map(n => s" ($n)").getOrElse(CommonBase.EMPTY) + TITLE_DESIGNATION_EXTRA
  }

  override def altName(): Option[String] = None

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(song.coverImage, song, data))

  override def mainContent(): List[BodyElement[?]] = {
    val mediaSection = MultiMediaCard.generateExtraMediaSection(song.multimedia, song.relatedTo, data, Song.FROM_KEY)

    div()
      .withClass(CommonBase.CLASS_MAIN_TITLE)
      .appendElements(
        LineCard.generate(CompiledData.getSong(song.id, data))
      )
      :: mediaSection
  }

}
