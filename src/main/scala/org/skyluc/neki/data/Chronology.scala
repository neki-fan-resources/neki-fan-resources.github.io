package org.skyluc.neki.data

import org.skyluc.neki.html.MarkerCompiledData
import org.skyluc.neki.html.page.ChronologySvg
import org.skyluc.neki.data.BaseMarker.BaseMarkerCompiledData
import org.skyluc.neki.html.ItemCompiledData
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.data.ShowMarker.ShowMarkerCompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.neki.data.MultiMediaMarker.MultiMediaMarkerCompiledData
import org.skyluc.neki.html.MultiMediaCompiledDataWithParentKey

case class Chronology(
    markers: List[ChronologyMarker],
    startDate: Date,
    endDate: Date,
) {
  def referencedIds(): List[Id[?]] = markers.flatMap(_.referencedIds())
}

trait ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData
  def referencedIds(): List[Id[?]]
}

object ChronologyMarker {
  trait PositionWrapper {
    val position: Position

    val up = position.up
    val in = position.in
  }

  trait ItemCompiledDataWrapper {
    val compiledData: ItemCompiledData

    val designation = Some(compiledData.designation)
    val label = compiledData.shortLabel.getOrElse(compiledData.label)
    val sublabel = compiledData.sublabel
    val image = Some(compiledData.coverUrl)
    val imageAlt = Some(compiledData.coverUrl)
    val item = Some(compiledData)
    val multimedia = None // TODO: move it out of here, to correctly support multimedia element
  }
}

case class BaseMarker(
    label: String,
    date: Date,
    image: String,
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    BaseMarkerCompiledData(
      label,
      date,
      Some(image),
      date.fromRefDay(refDay),
      position,
    )
  }

  def referencedIds(): List[Id[?]] = Nil
}

object BaseMarker {
  case class BaseMarkerCompiledData(label: String, date: Date, image: Option[String], day: Int, position: Position)
      extends MarkerCompiledData
      with ChronologyMarker.PositionWrapper {
    val id = ID_BASE + date.toStringSafe()
    val designation = None
    val sublabel = None
    val imageAlt = Some(label)

    val item = None
    val multimedia = None
    val left = true
    val short = false
    val `class` = ChronologySvg.CLASS_BASE_MARKER
  }

  val ID_BASE = "marker_"
}

case class ShowMarker(
    show: ShowId,
    short: Boolean,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    val compiledData = CompiledData.getShow(show, data)
    ShowMarkerCompiledData(show, compiledData, compiledData.date.fromRefDay(refDay), short, position)
  }

  def referencedIds(): List[Id[?]] = List(Some(show), relatedMultimedia).flatten
}

object ShowMarker {
  case class ShowMarkerCompiledData(
      showId: ShowId,
      compiledData: ItemCompiledData,
      day: Int,
      short: Boolean,
      position: Position,
  ) extends MarkerCompiledData
      with ChronologyMarker.ItemCompiledDataWrapper
      with ChronologyMarker.PositionWrapper {
    val id = showId.toString()
    val left = true
    val `class` = ChronologySvg.CLASS_SHOW_MARKER
  }
}

case class SongMarker(
    song: SongId,
    relatedMultimedia: Option[MultiMediaId],
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    val compiledData = CompiledData.getSong(song, data)
    SongMarker.SongMarkerCompiledData(song, compiledData, compiledData.date.fromRefDay(refDay), position)
  }
  def referencedIds(): List[Id[?]] = List(Some(song), relatedMultimedia).flatten
}

object SongMarker {
  case class SongMarkerCompiledData(songId: SongId, compiledData: ItemCompiledData, day: Int, position: Position)
      extends MarkerCompiledData
      with ChronologyMarker.ItemCompiledDataWrapper
      with ChronologyMarker.PositionWrapper {
    val id = songId.toString()

    override val sublabel: Option[String] = SUBLABEL
    val left = false
    val short = false
    val `class` = ChronologySvg.CLASS_SONG_MARKER
  }

  val SUBLABEL = Some("Release")
}

case class AlbumMarker(
    album: AlbumId,
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    val compiledData = CompiledData.getAlbum(album, data)
    AlbumMarker.AlbumMarkerCompiledDate(album, compiledData, compiledData.date.fromRefDay(refDay), position)
  }
  def referencedIds(): List[Id[?]] = List(album)
}

object AlbumMarker {
  case class AlbumMarkerCompiledDate(albumId: AlbumId, compiledData: ItemCompiledData, day: Int, position: Position)
      extends MarkerCompiledData
      with ChronologyMarker.ItemCompiledDataWrapper
      with ChronologyMarker.PositionWrapper {
    val id = albumId.toString()

    override val sublabel: Option[String] = SUBLABEL
    val left = false
    val short = false
    val `class` = ChronologySvg.CLASS_ALBUM_MARKER
  }

  val SUBLABEL = Some("Release")
}

case class MultiMediaMarker(
    multimedia: MultiMediaId,
    parentKey: String,
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    val compiledData = CompiledData.getMultiMedia(multimedia, data)
    val parent = compiledData.from.find(_._1 == parentKey).map(_._2).get
    MultiMediaMarkerCompiledData(
      multimedia,
      MultiMediaCompiledDataWithParentKey(
        compiledData,
        parentKey,
      ),
      compiledData.date.fromRefDay(refDay),
      parent,
      position,
    )
  }
  def referencedIds(): List[Id[?]] = List(multimedia)
}

object MultiMediaMarker {
  case class MultiMediaMarkerCompiledData(
      multimediaId: MultiMediaId,
      compiledData: MultiMediaCompiledDataWithParentKey,
      day: Int,
      parent: ItemCompiledData,
      position: Position,
  ) extends MarkerCompiledData
      with ChronologyMarker.PositionWrapper {
    val id = multimediaId.toString.replace('-', '_')
    val designation = Some(compiledData.compiledData.designation)
    val label = parent.label
    val sublabel = Some(compiledData.compiledData.label)
    val image = Some(compiledData.compiledData.imageUrl)
    val imageAlt = Some(compiledData.compiledData.label + MultiMediaCard.MEDIA_IMAGE_ALT)
    val item = None
    val multimedia = Some(compiledData)
    val left = false
    val short = false
    val `class` = ChronologySvg.CLASS_MULTIMEDIA_MARKER
  }
}

case class Position(
    up: Int,
    in: Int,
)
