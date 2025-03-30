package org.skyluc.neki.html.page

import org.skyluc.html.Html
import java.nio.file.Path
import org.skyluc.neki.data.Album
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._

class AlbumPage(val album: Album, data: Data) extends Page(data) {

  import AlbumPage._

  override def path(): Path = Path.of(ALBUM_PATH, album.id.id + Pages.HTML_EXTENSION)

  override def content(): Html = {
    CommonBase.generate(this)
  }

  override def shortTitle(): String = {
    album.fullname + CommonBase.SEPARATOR + album.designation
  }

  override def altName(): Option[String] = album.altname

}

object AlbumPage {
  final val ALBUM_PATH = "album"

}

