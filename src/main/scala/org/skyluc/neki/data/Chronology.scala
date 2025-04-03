package org.skyluc.neki.data

import org.skyluc.neki.html.MarkerCompiledData
import org.skyluc.neki.html.page.ChronologySvg
import org.skyluc.neki.data.BaseMarker.BaseMarkerCompiledData
import org.skyluc.neki.html.ItemCompiledData
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.data.ShowMarker.ShowMarkerCompiledData
import org.skyluc.neki.html.MultiMediaCompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.neki.data.MultiMediaMarker.MultiMediaMarkerCompiledData

case class Chronology(
    markers: List[ChronologyMarker],
    startDate: Date,
    endDate: Date,
)

trait ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData
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
    val label = compiledData.label
    val sublabel = compiledData.sublabel
    val image = Some(compiledData.coverUrl)
    val imageAlt = Some(compiledData.coverUrl)
    val parent = compiledData.parent
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
}

object BaseMarker {
  case class BaseMarkerCompiledData(label: String, date: Date, image: Option[String], day: Int, position: Position)
      extends MarkerCompiledData
      with ChronologyMarker.PositionWrapper {
    val id = ID_BASE + date.toString()
    val designation = None
    val sublabel = None
    val imageAlt = Some(label)

    val parent = None
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

case class MultiMediaMarker(
    multimedia: MultiMediaId,
    parentKey: String,
    position: Position,
) extends ChronologyMarker {
  def markerCompiledData(refDay: Int, data: Data): MarkerCompiledData = {
    val compiledData = CompiledData.getMultiMedia(multimedia, data)
    val parent = compiledData.from.find(_._1 == parentKey).map(_._2).get
    MultiMediaMarkerCompiledData(multimedia, compiledData, compiledData.date.fromRefDay(refDay), parent, position)
  }
}

object MultiMediaMarker {
  case class MultiMediaMarkerCompiledData(
      multimediaId: MultiMediaId,
      compiledData: MultiMediaCompiledData,
      day: Int,
      parent_ : ItemCompiledData,
      position: Position,
  ) extends MarkerCompiledData
      with ChronologyMarker.PositionWrapper {
    val id = multimediaId.toString
    val designation = Some(compiledData.designation)
    val label = parent_.label
    val sublabel = Some(compiledData.label)
    val image = Some(compiledData.imageUrl)
    val imageAlt = Some(compiledData.label + MultiMediaCard.MEDIA_IMAGE_ALT)
    val parent = Some(parent_)
    val left = false
    val short = false
    val `class` = ChronologySvg.CLASS_MULTIMEDIA_MARKER
  }
}

case class Position(
    up: Int,
    in: Int,
)
