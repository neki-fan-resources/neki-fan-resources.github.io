package org.skyluc.neki.html

import org.skyluc.html.Html
import java.nio.file.Path

case class AlbumPage(album: org.skyluc.neki.data.Album) extends Page {

  import AlbumPage._

  override def getPath(): Path = Path.of(ALBUM_PATH, album.id.id + Pages.HTML_EXTENSION)

  override def getContent(): Html = {
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

