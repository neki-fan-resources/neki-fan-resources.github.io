package org.skyluc.neki.html.page

import org.skyluc.html.Html
import java.nio.file.Path
import org.skyluc.neki.data.Album
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement

class AlbumPage(val album: Album, data: Data) extends Page(data) {

  import AlbumPage._

  val compiledData: ItemCompiledData = {
    val info =
      List(ItemInfo(CompiledData.LABEL_RELEASED, album.releaseDate.toString()))
    ItemCompiledData(
      album.designation,
      album.fullname,
      None,
      "", // TODO
      "", // TODO
      info,
    )
  }

  override def path(): Path = Path.of(ALBUM_PATH, album.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = {
    album.fullname + CommonBase.SEPARATOR + album.designation
  }

  override def altName(): Option[String] = album.altname

  override def mainContent(): List[BodyElement[?]] = {
    List(
      ItemDetails.generate(compiledData)
    )
  }

}

object AlbumPage {
  val ALBUM_PATH = "album"

}

