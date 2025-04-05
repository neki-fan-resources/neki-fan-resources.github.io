package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Album
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement
import org.skyluc.neki.data.MultiMediaId

class AlbumPage(val album: Album, extraPage: Boolean, data: Data) extends Page(data) {

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
      Album.FROM_KEY,
    )

    val extraSection = if (extraPage) {
      List(ExtraLink.generate("/" + extraPath(album).toString()))
    } else {
      Nil
    }

    List(
      ItemDetails.generate(CompiledData.getAlbum(album.id, data)),
      SectionHeader.generate(SECTION_SONGS),
      MediumCard.generateList(
        album.songs.map(CompiledData.getSong(_, data))
      ),
    ) ::: additionalSection ::: extraSection
  }

}

object AlbumPage {
  val ALBUM_PATH = "album"
  val DESIGNATION = "Album"

  val SECTION_SONGS = "Songs"

  val SECTION_ADDITIONAL_TEXT = "Additional"

  def extraPath(album: Album): Path = Path.of(ALBUM_PATH, Pages.EXTRA_PATH, album.id.id + Pages.HTML_EXTENSION)

  def extraMultimedia(album: Album): List[MultiMediaId] = {
    val allMultiMediaInMainPage = album.multimedia.all()
    val allRelatedMultiMedia: List[MultiMediaId] = album.relatedTo.flatMap {
      case m: MultiMediaId =>
        Some(m)
      case _ =>
        None
    }
    allRelatedMultiMedia.filterNot(allMultiMediaInMainPage.contains(_))
  }

  def pagesFor(album: Album, data: Data): List[Page] = {
    if (extraMultimedia(album).isEmpty) {
      List(AlbumPage(album, false, data))
    } else {
      List(AlbumPage(album, true, data), AlbumExtraPage(album, data))
    }
  }
}
