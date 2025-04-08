package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.data.Album
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.CommonBase
import org.skyluc.neki.html.LineCard
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.html.Html._
import org.skyluc.neki.html.CoverImage

class AlbumExtraPage(album: Album, data: Data) extends Page(data) {

  import AlbumPage._

  override def path(): Path = extraPath(album)

  override def shortTitle(): String = {
    album.fullname + CommonBase.SEPARATOR + album.designation + " Extra"
  }

  override def altName(): Option[String] = album.altname

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(album.coverImage, album, data))

  override def mainContent(): List[BodyElement[?]] = {
    val em = extraMultimedia(album)
    val mediaSection = if (em.isEmpty) {
      Nil
    } else {
      val sorted = em.map(CompiledData.getMultiMedia(_, data)).sortBy(_.date).reverse
      MultiMediaCard.generateSection(
        "Media",
        sorted,
        Album.FROM_KEY,
      )
    }
    div()
      .withClass(CommonBase.CLASS_MAIN_TITLE)
      .appendElements(
        LineCard.generate(CompiledData.getAlbum(album.id, data))
      )
      :: mediaSection
  }

}
