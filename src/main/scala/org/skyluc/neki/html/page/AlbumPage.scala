package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.data.Album
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement

class AlbumPage(val album: Album, extraPage: Boolean, data: Data) extends Page(data) {

  import AlbumPage._

  override def path(): Path = Path.of(ALBUM_PATH, album.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    album.fullname + CommonBase.SEPARATOR + album.designation
  }

  override def altName(): Option[String] = album.altname

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(album.coverImage, album, data))

  override def mainContent(): List[BodyElement[?]] = {

    val mainSections = MultiMediaCard.generateMainSections(album.multimedia, data, Album.FROM_KEY)

    val additionalSection = MultiMediaCard.generateAdditionalSection(album.multimedia, data, Album.FROM_KEY)

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
    ) ::: mainSections ::: additionalSection ::: extraSection
  }

}

object AlbumPage {
  val ALBUM_PATH = "album"
  val DESIGNATION = "Album"

  val SECTION_SONGS = "Songs"

  def extraPath(album: Album): Path = Path.of(ALBUM_PATH, Pages.EXTRA_PATH, album.id.id + Pages.HTML_EXTENSION)

  def pagesFor(album: Album, data: Data): List[Page] = {
    if (album.multimedia.extra(album.relatedTo).isEmpty) {
      List(AlbumPage(album, false, data))
    } else {
      List(AlbumPage(album, true, data), AlbumExtraPage(album, data))
    }
  }
}
