package org.skyluc.neki.html

import scala.collection.immutable.HashMap
import org.skyluc.neki.data.{Pages as dPages, *}
import org.skyluc.neki.html.page.AlbumPage
import org.skyluc.neki.html.page.SongPage

case class ItemCompiledData(
    url: String,
    designation: String,
    label: String,
    sublabel: Option[String],
    parent: Option[ItemCompiledData],
    date: Option[String],
    coverUrl: String,
    coverAlt: String,
    info: List[ItemInfo],
)

case class ItemInfo(
    label: Option[String],
    value: String,
)

type ItemCompiledDataTree = List[ItemCompiledDataNode]

case class ItemCompiledDataNode(
    data: ItemCompiledData,
    subNodes: ItemCompiledDataTree,
)

object ItemInfo {
  def apply(label: String, value: String): ItemInfo =
    ItemInfo(Some(label), value)
}

object CompiledData {

  val LABEL_RELEASED = "released"

  var cache: Map[Id, ItemCompiledData] = HashMap()

  def getAlbum(id: AlbumId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForAlbum(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def getSong(id: SongId, data: Data): ItemCompiledData = {
    cache.get(id) match {
      case Some(compiledData) =>
        compiledData
      case None =>
        val compiledData = compileForSong(id, data)
        cache += ((id, compiledData))
        compiledData
    }
  }

  def compileForAlbum(id: AlbumId, data: Data): ItemCompiledData = {
    val album = data.albums(id)
    val info =
      List(ItemInfo(CompiledData.LABEL_RELEASED, album.releaseDate.toString()))
    ItemCompiledData(
      Album.URL_BASE + id.id + Pages.HTML_EXTENSION,
      album.designation,
      album.fullname,
      None,
      None,
      None,
      CoverImage.resolveUrl(album.coverImage, album, data),
      CoverImage.buildAlt(AlbumPage.DESIGNATION, album.fullname),
      info,
    )
  }

  def compileForSong(id: SongId, data: Data): ItemCompiledData = {
    val song = data.songs(id)
    val info =
      ItemInfo(CompiledData.LABEL_RELEASED, song.releaseDate.toString()) ::
        song.credits
          .map { credits =>
            List(
              ItemInfo(SongPage.LABEL_LYRICIST, credits.lyricist),
              ItemInfo(SongPage.LABEL_COMPOSER, credits.composer),
            )
          }
          .getOrElse(Nil)

    ItemCompiledData(
      Song.URL_BASE + id.id + Pages.HTML_EXTENSION,
      SongPage.DESIGNATION,
      song.fullname,
      song.fullnameEn,
      song.album.map(getAlbum(_, data)),
      None,
      CoverImage.resolveUrl(song.coverImage, song, data),
      CoverImage.buildAlt(SongPage.DESIGNATION, song.fullname),
      info,
    )
  }

  // ------------
}
