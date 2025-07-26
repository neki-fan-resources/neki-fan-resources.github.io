package org.skyluc.neki_site.html.component

import org.skyluc.fan_resources.data.Date
import org.skyluc.fan_resources.data.Date.DateTick
import org.skyluc.fan_resources.data.DisplayMetadata
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.html.*

import Html.*
import SvgElement.{text as svgText, *}

object TimelineBlock {

  val CLASS_BLOCK = "timeline-block"

  case class Tick(
      tick: DateTick,
      `class`: String,
      left: Boolean,
  )

  def generate(
      startDate: Date,
      endDate: Date,
      compiledData: Seq[ElementCompiledData],
      metadata: Map[String, DisplayMetadata],
  ): Svg = {

    val refDay = startDate.epochDay()
    val endDay = endDate.fromRefDay(refDay)

    svg()
      .withClass(CLASS_BLOCK)
      .appendElements(style(SVG_STYLE))
      .withViewBox(-90, -endDay - 15, 180, endDay + 25)
      .appendElements(line(0, -endDay, 0, 0).withStroke("black").withClass(CLASS_LINE))
      .appendElements(
        generateTicks(startDate, endDate, refDay)
      )
      .appendElements(
        generateMarkers(compiledData, refDay, metadata)*
      )

  }

  def generateTicks(startDate: Date, endDate: Date, refDay: Int): SvgG = {

    val ticks =
      Date.yearIntervals(startDate, endDate, refDay).map(Tick(_, CLASS_TICK_YEAR, false))
        ::: Date.monthIntervals(startDate, endDate, refDay).map(Tick(_, CLASS_TICK_MONTH, true))

    g().appendElements(
      ticks.map(generateTick)*
    )
  }

  def generateTick(tick: Tick): SvgText = {
    val x = if (tick.left) -3 else 4
    svgText(x, -tick.tick.day, tick.tick.label).withClass(tick.`class`)
  }

  private def generateMarkers(
      compiledData: Seq[ElementCompiledData],
      refDay: Int,
      metadata: Map[String, DisplayMetadata],
  ): Seq[SvgElement[?]] = {
    compiledData.map(compiledData =>
      generateMarker(compiledData, metadata.getOrElse(compiledData.uId, DisplayMetadata.ZERO), refDay)
    )
  }

  private def designationToId(designation: String): String = {
    designation.filterNot(_ == ' ').toLowerCase()
  }

  private def generateMarker(
      compiledData: ElementCompiledData,
      metadata: DisplayMetadata,
      refDay: Int,
  ): SvgG = {
    val day = compiledData.date.fromRefDay(refDay)

    val xSign = if (metadata.left) -1 else 1
    val yMod = -metadata.forward / 2.0
    val inMod = metadata.in / 2.0

    val line = path(s"M 0 0 h${(5 - inMod) * xSign} v${yMod} h${(5 + inMod) * xSign}")

    val elements: List[SvgElement[?]] = if (metadata.short) {
      List(svgText(12 * xSign, 0.25 + yMod, compiledData.label).withClass(CLASS_MARKER_LABEL_SHORT))
    } else {
      List(
        Some(rect(15 * xSign - 5, -5 + yMod, 10, 10)),
        Some(
          image(15 * xSign - 5, -5 + yMod, 10, 10, compiledData.cover.source.toString())
            .withPreserveAspectRatio("xMidYMid slice")
            .withClass(CLASS_MARKER_IMAGE)
        ),
        Some(svgText(21 * xSign, 0.25 + yMod, compiledData.label).withClass(CLASS_MARKER_LABEL)),
        Some(svgText(21.25 * xSign, -3 + yMod, compiledData.designation).withClass(CLASS_MARKER_DESIGNATION)),
        compiledData.sublabel.map(sublabel =>
          svgText(21.25 * xSign, 3.5 + yMod, sublabel).withClass(CLASS_MARKER_SUBLABEL)
        ),
      ).flatten
    }

    g()
      .withId(compiledData.uId)
      .withClass(if (metadata.left) CLASS_MARKER_LEFT else CLASS_MARKER_RIGHT)
      .withClass(s"timeline_${designationToId(compiledData.designation)}_marker")
      .appendElements(line)
      .appendElements(
        g()
          .withClass(CLASS_MARKER_BLOCK)
          .withOnClick(s"toggleOverlay('${compiledData.uId}')")
          .appendElements(
            elements*
          )
      )
      .withTransform(s"translate(0, ${-day})")
  }

  val CLASS_LINE = "timeline_line"
  val CLASS_TICK_YEAR = "timeline_tick_year"
  val CLASS_TICK_MONTH = "timeline_tick_month"

  val CLASS_MARKER_LEFT = "timeline_marker_left"
  val CLASS_MARKER_RIGHT = "timeline_marker_right"
  val CLASS_MARKER_BLOCK = "timeline_marker_block"

  val CLASS_MARKER_DESIGNATION = "timeline_marker_designation"
  val CLASS_MARKER_LABEL = "timeline_marker_label"
  val CLASS_MARKER_LABEL_SHORT = "timeline_marker_label_short"
  val CLASS_MARKER_SUBLABEL = "timeline_marker_sublabel"
  val CLASS_MARKER_IMAGE = "timeline_marker_image"

  // categories (using designation for now TODO)
  val CLASS_RADIO_SHOW_MARKER = "timeline_radioshow_marker"
  val CLASS_MAGAZINE_MARKER = "timeline_magazine_marker"
  val CLASS_SHOW_MARKER = "timeline_show_marker"
  val CLASS_SONG_MARKER = "timeline_song_marker"
  val CLASS_ALBUM_MARKER = "timeline_album_marker"
  val CLASS_MULTIMEDIA_MARKER = "timeline_youtubevideo_marker"

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

.$CLASS_MARKER_LEFT path,
.$CLASS_MARKER_RIGHT path {
  stroke: black;
  stroke-width: 0.25px;
  fill: none;
}
}

.$CLASS_MARKER_BLOCK {
  cursor: pointer;
}

.$CLASS_MARKER_BLOCK text {
  alignment-baseline: middle;
}

.$CLASS_MARKER_BLOCK rect {
  stroke: black;
  stroke-width: 0.25px;
  fill: black;
}

.$CLASS_MARKER_LEFT text {
  text-anchor: end;
}

.$CLASS_MARKER_DESIGNATION {
  font-size: 2.5px;
  font-weight: 600;
  text-transform: uppercase;
  fill: var(--color-designation);
}

.$CLASS_MARKER_LABEL {
  font-size: 4.5px;
  font-weight: 600;
}

.$CLASS_MARKER_LABEL_SHORT{
  font-size: 3px;
  font-weight: 600;
}

.$CLASS_MARKER_SUBLABEL {
  font-size: 3px;
  font-weight: 500;
}

.$CLASS_MARKER_IMAGE {
  preserveAspectRation: xMaxYMax slice;
}

.$CLASS_RADIO_SHOW_MARKER,
.$CLASS_MAGAZINE_MARKER {
  fill: darkblue;
}

  """
}
