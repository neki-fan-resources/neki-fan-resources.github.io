package org.skyluc.neki.html.page

import org.skyluc.neki.data.{ChronologyPage => dChronologyPage, Data}
import org.skyluc.neki.html.Page
import org.skyluc.html._
import Html._
import SvgElement.{text => svgText, _}
import java.nio.file.Path
import org.skyluc.neki.html.Pages
import org.skyluc.neki.data.Date
import org.skyluc.neki.data.Date.DateTick
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MarkerCompiledData

class ChronologyPage(page: dChronologyPage, data: Data) extends Page(data) {

  import ChronologyPage._
  import ChronologySvg._

  override def path(): Path = Path.of(CHRONOLOGY_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val chronology = page.chronology
    val refDay = chronology.startDate.epochDay()
    val endDay = chronology.endDate.fromRefDay(refDay)

    val markersCompiledData = page.chronology.markers.map(_.markerCompiledData(refDay, data))

    val ticks =
      Date.yearIntervals(chronology.startDate, chronology.endDate, refDay).map(Tick(_, CLASS_TICK_YEAR, false))
        ::: Date.monthIntervals(chronology.startDate, chronology.endDate, refDay).map(Tick(_, CLASS_TICK_MONTH, true))

    List(
      ChronologySvg.generate(endDay, ticks, markersCompiledData)
    )
  }

}

object ChronologyPage {
  val CHRONOLOGY_PATH = "chronology" + Pages.HTML_EXTENSION
  val DESIGNATION = "Chronology"

  // TODO ?
  // val MAIN_INTRO_TEXT = "Videos of live performances by NEK!."
}

object ChronologySvg {

  case class Tick(
      tick: DateTick,
      `class`: String,
      left: Boolean,
  )

  def generate(endDay: Int, ticks: List[Tick], markers: List[MarkerCompiledData]): Svg = {
    svg()
      .withClass(CLASS_SVG)
      .withViewBox(-90, -10, 180, endDay + 25)
      .appendElements(style(SVG_STYLE))
      .appendElements(line(0, 0, 0, endDay).withStroke("black").withClass(CLASS_LINE))
      .appendElements(
        generateTicks(ticks)
      )
      .appendElements(
        generateMarkers(markers)*
      )
  }

  private def generateTicks(ticks: List[Tick]): SvgG = {
    g().appendElements(
      ticks.map(generateTick)*
    )
  }

  private def generateTick(tick: Tick): SvgText = {
    val x = if (tick.left) -3 else 4
    svgText(x, tick.tick.day, tick.tick.label).withClass(tick.`class`)
  }

  private def generateMarkers(markers: List[MarkerCompiledData]): List[SvgElement[?]] = {
    markers.map(generateMarker)
  }

  private def generateMarker(marker: MarkerCompiledData): SvgG = {
    val xSign = if (marker.left) -1 else 1
    val yMod = -marker.up / 2.0

    val line = path(s"M 0 0 h${5 * xSign} v${yMod} h${5 * xSign}")

    val elements: List[SvgElement[?]] = if (marker.short) {
      List(svgText(13 * xSign, 0.25 + yMod, marker.label).withClass(CLASS_CHRONOLOGY_MARKER_LABEL_SHORT))
    } else {
      List(
        Some(rect(15 * xSign - 5, -5 + yMod, 10, 10)),
        marker.image.map(imageUrl => image(15 * xSign - 5, -5 + yMod, 10, 10, imageUrl)),
        Some(svgText(22 * xSign, 0.25 + yMod, marker.label).withClass(CLASS_CHRONOLOGY_MARKER_LABEL)),
        marker.designation.map(designation =>
          svgText(22.25 * xSign, -3.5 + yMod, designation).withClass(CLASS_CHRONOLOGY_MARKER_DESIGNATION)
        ),
        marker.sublabel.map(sublabel =>
          svgText(22.25 * xSign, 4 + yMod, sublabel).withClass(CLASS_CHRONOLOGY_MARKER_SUBLABEL)
        ),
      ).flatten
    }

    g()
      .withClass(if (marker.left) CLASS_CHRONOLOGY_MARKER_LEFT else CLASS_CHRONOLOGY_MARKER_RIGHT)
      .withClass(marker.`class`)
      .appendElements(line)
      .appendElements(
        elements*
      )
      .withTransform(s"translate(0, ${marker.day})")
  }

  // -----------

  val CLASS_SVG = "chronology-svg"
  val CLASS_LINE = "chronology_line"
  val CLASS_TICK_YEAR = "chronology_tick_year"
  val CLASS_TICK_MONTH = "chronology_tick_month"

  val CLASS_CHRONOLOGY_MARKER_LEFT = "chronology_marker_left"
  val CLASS_CHRONOLOGY_MARKER_RIGHT = "chronology_marker_right"

  val CLASS_CHRONOLOGY_MARKER_DESIGNATION = "chronology_marker_designation"
  val CLASS_CHRONOLOGY_MARKER_LABEL = "chronology_marker_label"
  val CLASS_CHRONOLOGY_MARKER_SUBLABEL = "chronology_marker_sublabel"
  val CLASS_CHRONOLOGY_MARKER_LABEL_SHORT = "chronology_marker_label_short"

  val CLASS_BASE_MARKER = "chronology_base_marker"
  val CLASS_SHOW_MARKER = "chronology_show_marker"
  val CLASS_SONG_MARKER = "chronology_song_marker"
  val CLASS_MULTIMEDIA_MARKER = "chronology_multimedia_marker"

  val SVG_STYLE = s"""
.$CLASS_LINE {
  stroke: black;
  stroke-width: 0.75px;
}

.$CLASS_TICK_MONTH,
.$CLASS_TICK_YEAR {
  fill: lightslategray;
  font-weight: 600;
  alignment-baseline: middle;
}
.$CLASS_TICK_MONTH {
  font-size: 3px;
  text-anchor: end;
}
.$CLASS_TICK_YEAR {
  font-size: 4px;
  text-anchor: start;
}

.$CLASS_CHRONOLOGY_MARKER_LEFT text,
.$CLASS_CHRONOLOGY_MARKER_RIGHT text {
  alignment-baseline: middle;
}

.$CLASS_CHRONOLOGY_MARKER_LEFT path,
.$CLASS_CHRONOLOGY_MARKER_RIGHT path {
  stroke: black;
  stroke-width: 0.25px;
  fill: none;
}

.$CLASS_CHRONOLOGY_MARKER_LEFT rect,
.$CLASS_CHRONOLOGY_MARKER_RIGHT rect {
  stroke: black;
  stroke-width: 0.25px;
  fill: black;
}

.$CLASS_CHRONOLOGY_MARKER_LEFT text {
  text-anchor: end;
}

.$CLASS_CHRONOLOGY_MARKER_DESIGNATION {
  font-size: 2.5px;
  font-weight: 600;
  text-transform: uppercase;
  fill: var(--color-designation);
}

.$CLASS_CHRONOLOGY_MARKER_LABEL {
  font-size: 5px;
  font-weight: 600;
}

.$CLASS_CHRONOLOGY_MARKER_LABEL_SHORT{

  font-size: 4px;
  font-weight: 600;
}

.$CLASS_CHRONOLOGY_MARKER_SUBLABEL {
  font-size: 3px;
  font-weight: 500;
}
"""
}
