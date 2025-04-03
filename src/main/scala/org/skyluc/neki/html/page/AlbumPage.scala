package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Album
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement

class AlbumPage(val album: Album, data: Data) extends Page(data) {

  import AlbumPage._

  override def path(): Path = Path.of(ALBUM_PATH, album.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    album.fullname + CommonBase.SEPARATOR + album.designation
  }

  override def altName(): Option[String] = album.altname

  override def mainContent(): List[BodyElement[?]] = {
    val additionalSection = MultiMediaCard.generateSection(
      SECTION_ADDITIONAL_TEXT,
      CompiledData.getMultiMedia(album.multimedia.additional, data),
    )

    List(
      ItemDetails.generate(CompiledData.getAlbum(album.id, data)),
      SectionHeader.generate(SECTION_SONGS),
      MediumCard.generateList(
        album.songs.map(CompiledData.getSong(_, data))
      ),
    ) ::: additionalSection
  }

}

object AlbumPage {
  val ALBUM_PATH = "album"
  val DESIGNATION = "Album"

  val SECTION_SONGS = "Songs"

  val SECTION_ADDITIONAL_TEXT = "Additional"
}
