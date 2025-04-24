package org.skyluc.fan_resources.html

import org.skyluc.fan_resources.data.Date

case class ElementCompiledData(
    designation: String,
    label: String,
    shortLabel: Option[String],
    sublabel: Option[String],
    parent: Option[ElementCompiledData],
    description: Option[List[String]],
    date: Date,
    displayDate: Option[String],
    cover: ImageCompiledData,
    info: List[ElementInfo],
    targetUrl: Url,
    fromKey: String,
)

case class ElementInfo(
    label: Option[String],
    value: String,
    url: Option[Url],
    infoLevel: Int,
)

object ElementInfo {
  val INFO_LEVEL_NONE = 0
  val INFO_LEVEL_MINIMUM = 1
  val INFO_LEVEL_BASIC = 2
  val INFO_LEVEL_ALL = 3

  def apply(label: String, value: String, url: Url, infoLevel: Int): ElementInfo =
    ElementInfo(Some(label), value, Some(url), infoLevel)
  def apply(label: String, value: String, infoLevel: Int): ElementInfo =
    ElementInfo(Some(label), value, None, infoLevel)

  def dateDepending(
      labelBefore: String,
      labelAfter: String,
      cutoffDate: Date,
      value: String,
      infoLevel: Int,
  ): ElementInfo = {
    if (cutoffDate.isPast()) {
      ElementInfo(labelAfter, value, infoLevel)
    } else {
      ElementInfo(labelBefore, value, infoLevel)
    }
  }
}

case class MultiMediaCompiledData(
    designation: String,
    label: String,
    date: Date,
    image: ImageWithOverlayCompiledData,
    info: Option[String],
    froms: Seq[(String, ElementCompiledData)],
    available: Boolean,
    targetUrl: Url,
)

case class MultiMediaBlockCompiledData(
    video: List[MultiMediaCompiledData],
    live: List[MultiMediaCompiledData],
    concert: List[MultiMediaCompiledData],
    short: List[MultiMediaCompiledData],
    image: List[MultiMediaCompiledData],
    additional: List[MultiMediaCompiledData],
    extra: Seq[MultiMediaCompiledData],
)

case class ImageCompiledData(
    source: Url,
    alt: String,
    targetUrl: Option[Url],
)

// TODO-NOW: still needed ?
object ImageCompiledData {
  def apply(image: ImageWithOverlayCompiledData): ImageCompiledData =
    ImageCompiledData(image.source, image.alt, None)
}

// TODO-NOW: rename
case class ImageWithOverlayCompiledData(
    source: Url,
    overlaySource: Url,
    alt: String,
    overlayAlt: String,
    centerOverlay: Boolean,
) {
  def toImageCompiledData(): ImageCompiledData = ImageCompiledData(this)
}

case class MarkerCompiledData(
    id: String,
    marker: MarkerCompiledDataMarker,
    details: MarkerCompiledDataDetails,
)

case class MarkerCompiledDataMarker(
    designation: Option[String],
    label: String,
    sublabel: Option[String],
    image: ImageCompiledData,
    day: Int,
    `class`: String,
    left: Boolean,
    short: Boolean,
    up: Int,
    in: Int,
)

case class MarkerCompiledDataDetails(
    `type`: Int,
    item: Option[ElementCompiledData],
    multimedia: Option[MultiMediaCompiledData],
    multimediaFromKey: String,
)

object MarkerCompiledData {
  val DETAILS_BASIC = 1
  val DETAILS_ELEMENT = 2
  val DETAILS_MULTIMEDIA = 3
}
