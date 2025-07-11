package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Date
import org.skyluc.fan_resources.data.Date.DateTick
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.MarkerCompiledData
import org.skyluc.fan_resources.html.MarkerCompiledDataMarker
import org.skyluc.fan_resources.html.component.ChronologyMarkerDetails
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.html.*
import org.skyluc.neki_site.data.ChronologyPage as dChronologyPage
import org.skyluc.neki_site.data.Site as dSite
import org.skyluc.neki_site.html.ChronoloyMarkerProcessor
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*
import SvgElement.{text as svgText, *}

class ChronologyPage(
    markersCompiledData: Seq[MarkerCompiledData],
    refDay: Int,
    startDate: Date,
    endDate: Date,
    description: PageDescription,
    site: dSite,
) extends SitePage(description, site) {

  import ChronologyPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    val mainIntro = MainIntro.generate(MAIN_INTRO_TEXT)

    val svgElement = ChronologySvg.generate(startDate, endDate, refDay, markersCompiledData)

    val overlaysData = ChronologyMarkerDetails.generateOverlayContent(markersCompiledData)
    val categorisData = ChronologyMarkerDetails.generateCategories(markersCompiledData)

    List(
      mainIntro,
      svgElement,
      overlaysData,
      categorisData,
    )

  }
}

object ChronologyPage {

  val PAGE_PATH = Path("chronology")

  val MAIN_INTRO_TEXT = "The main events in the NEK! story. Song and EP releases, shows, tours, interviews, ..."

  def pagesFor(chronologyPage: dChronologyPage, site: dSite, generator: CompiledDataGenerator): Seq[SitePage] = {
    val refDay = chronologyPage.chronology.startDate.epochDay()

    val processor = ChronoloyMarkerProcessor(refDay, generator)

    val markersCompiledData = chronologyPage.chronology.markers.map(processor.process)

    val mainPage =
      ChronologyPage(
        markersCompiledData,
        refDay,
        chronologyPage.chronology.startDate,
        chronologyPage.chronology.endDate,
        PageDescription(
          TitleAndDescription.formattedTitle(
            None,
            None,
            "Chronology",
            None,
            None,
            None,
          ),
          TitleAndDescription.formattedDescription(
            None,
            None,
            "Chronology",
            None,
            None,
            None,
          ),
          SitePage.absoluteUrl(Site.DEFAULT_COVER_IMAGE.source),
          SitePage.canonicalUrlFor(PAGE_PATH),
          PAGE_PATH.withExtension(Common.HTML_EXTENSION),
          None,
          None,
          false,
        ),
        site,
      )

    Seq(mainPage)
  }
}

object ChronologySvg {

  case class Tick(
      tick: DateTick,
      `class`: String,
      left: Boolean,
  )

  def generate(startDate: Date, endDate: Date, refDay: Int, markersCompiledData: Seq[MarkerCompiledData]): Svg = {

    val endDay = endDate.fromRefDay(refDay)

    svg()
      .withClass(CLASS_SVG)
      .withViewBox(-90, -10, 180, endDay + 25)
      .appendElements(style(SVG_STYLE))
      .appendElements(line(0, 0, 0, endDay).withStroke("black").withClass(CLASS_LINE))
      .appendElements(
        generateTicks(startDate, endDate, refDay)
      )
      .appendElements(
        generateMarkers(markersCompiledData)*
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
    svgText(x, tick.tick.day, tick.tick.label).withClass(tick.`class`)
  }

  private def generateMarkers(markers: Seq[MarkerCompiledData]): Seq[SvgElement[?]] = {
    markers.map(marker => generateMarker(marker.id.uId(), marker.marker))
  }

  private def generateMarker(id: String, marker: MarkerCompiledDataMarker): SvgG = {
    val xSign = if (marker.left) -1 else 1
    val yMod = -marker.up / 2.0
    val inMod = marker.in / 2.0

    val line = path(s"M 0 0 h${(5 - inMod) * xSign} v${yMod} h${(5 + inMod) * xSign}")

    val elements: List[SvgElement[?]] = if (marker.short) {
      val label = marker.sublabel.map(sublabel => marker.label).getOrElse(marker.label)
      List(svgText(12 * xSign, 0.25 + yMod, label).withClass(CLASS_CHRONOLOGY_MARKER_LABEL_SHORT))
    } else {
      List(
        Some(rect(15 * xSign - 5, -5 + yMod, 10, 10)),
        Some(
          image(15 * xSign - 5, -5 + yMod, 10, 10, marker.image.source.toString())
            .withPreserveAspectRatio("xMidYMid slice")
            .withClass(CLASS_CHRONOLOGY_MARKER_IMAGE)
        ),
        Some(svgText(21 * xSign, 0.25 + yMod, marker.label).withClass(CLASS_CHRONOLOGY_MARKER_LABEL)),
        marker.designation.map(designation =>
          svgText(21.25 * xSign, -3 + yMod, designation).withClass(CLASS_CHRONOLOGY_MARKER_DESIGNATION)
        ),
        marker.sublabel.map(sublabel =>
          svgText(21.25 * xSign, 3.5 + yMod, sublabel).withClass(CLASS_CHRONOLOGY_MARKER_SUBLABEL)
        ),
      ).flatten
    }

    g()
      .withClass(if (marker.left) CLASS_CHRONOLOGY_MARKER_LEFT else CLASS_CHRONOLOGY_MARKER_RIGHT)
      .withClass(s"chronology_${marker.`class`}_marker")
      .appendElements(line)
      .appendElements(
        g()
          .withClass(CLASS_CHRONOLOGY_MARKER_BLOCK)
          .withOnClick(s"toggleOverlay('${id}')")
          .appendElements(
            elements*
          )
      )
      .withTransform(s"translate(0, ${marker.day})")

  }

  val CLASS_SVG = "chronology-svg"
  val CLASS_LINE = "chronology_line"
  val CLASS_TICK_YEAR = "chronology_tick_year"
  val CLASS_TICK_MONTH = "chronology_tick_month"

  val CLASS_CHRONOLOGY_MARKER_LEFT = "chronology_marker_left"
  val CLASS_CHRONOLOGY_MARKER_RIGHT = "chronology_marker_right"
  val CLASS_CHRONOLOGY_MARKER_BLOCK = "chronology_marker_block"

  val CLASS_CHRONOLOGY_MARKER_DESIGNATION = "chronology_marker_designation"
  val CLASS_CHRONOLOGY_MARKER_LABEL = "chronology_marker_label"
  val CLASS_CHRONOLOGY_MARKER_LABEL_SHORT = "chronology_marker_label_short"
  val CLASS_CHRONOLOGY_MARKER_SUBLABEL = "chronology_marker_sublabel"
  val CLASS_CHRONOLOGY_MARKER_IMAGE = "chronology_marker_image"

  // related to MarkerCompiledDataMarker.class
  val CLASS_BASE_MARKER = "chronology_base_marker"
  val CLASS_MEDIA_MARKER = "chronology_media_marker"
  val CLASS_SHOW_MARKER = "chronology_show_marker"
  val CLASS_SONG_MARKER = "chronology_song_marker"
  val CLASS_ALBUM_MARKER = "chronology_album_marker"
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


.$CLASS_CHRONOLOGY_MARKER_LEFT path,
.$CLASS_CHRONOLOGY_MARKER_RIGHT path {
  stroke: black;
  stroke-width: 0.25px;
  fill: none;
}

.$CLASS_CHRONOLOGY_MARKER_BLOCK {
  cursor: pointer;
}

.$CLASS_CHRONOLOGY_MARKER_BLOCK text {
  alignment-baseline: middle;
}

.$CLASS_CHRONOLOGY_MARKER_BLOCK rect {
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
  font-size: 4.5px;
  font-weight: 600;
}

.$CLASS_CHRONOLOGY_MARKER_LABEL_SHORT{

  font-size: 3px;
  font-weight: 600;
}

.$CLASS_CHRONOLOGY_MARKER_SUBLABEL {
  font-size: 3px;
  font-weight: 500;
}

.$CLASS_CHRONOLOGY_MARKER_IMAGE {
  preserveAspectRation: xMaxYMax slice;
}

.$CLASS_MEDIA_MARKER {
  fill: darkblue;
}
"""

}
