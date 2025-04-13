package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object MultiMediaCard {

  def generateList(list: List[MultiMediaCompiledData], fromFilter: String): Div = {
    div()
      .withClass(CLASS_MULTIMEDIA_CARD_LIST)
      .appendElements(
        list.map(generate(_, fromFilter))*
      )
  }

  def generate(entry: MultiMediaCompiledDataWithParentKey): Div = {
    generate(entry.compiledData, entry.parentKey)
  }

  def generate(entry: MultiMediaCompiledData, fromFilter: String): Div = {
    val infoParts =
      List(
        entry.from.filterNot(_._1 == fromFilter).map(_._2.labelWithSublabel()),
        entry.info,
      ).flatten

    val label1 = div().withClass(CLASS_MULTIMEDIA_CARD_LABEL).appendElements(text(entry.label))
    val label2 = if (entry.available) {
      label1
    } else {
      label1.withClass(CLASS_MULTIMEDIA_CARD_LABEL_UNAVAILABLE)
    }
    val elements: List[BodyElement[?]] = List(
      label2,
      generateLayeredImage(entry),
      div().withClass(CLASS_MULTIMEDIA_CARD_SUBLABEL).appendElements(text(infoParts.mkString(", "))),
    )
    div()
      .withClass(CLASS_MULTIMEDIA_CARD)
      .appendElements(
        elements*
      )
  }

  def generateLayeredImage(entry: MultiMediaCompiledData): A = {
    a()
      .withClass(CLASS_MULTIMEDIA_CARD_IMAGE)
      .withHref(entry.url)
      .withTarget(A.TARGET_BLANK)
      .appendElements(
        img()
          .withClass(
            if (entry.centerOverlay) CLASS_MULTIMEDIA_CARD_IMAGE_CENTER_OVER else CLASS_MULTIMEDIA_CARD_IMAGE_TOP_OVER
          )
          .withSrc(BASE_OVERLAY_URL + entry.overlay)
          .withAlt(MEDIA_SERVICE_LOGO_ALT),
        img()
          .withClass(CLASS_MULTIMEDIA_CARD_IMAGE_UNDER)
          .withSrc(entry.imageUrl)
          .withAlt(entry.label + MEDIA_IMAGE_ALT),
      )
  }

  def generateSection(
      sectionText: String,
      list: List[MultiMediaCompiledData],
      fromFilter: String,
  ): List[BodyElement[?]] = {
    if (list.isEmpty) {
      Nil
    } else {
      List(
        SectionHeader.generate(sectionText),
        MultiMediaCard.generateList(list, fromFilter),
      )
    }
  }

  // ----------------

  val CLASS_MULTIMEDIA_CARD_LIST = "multimedia-card-list"
  val CLASS_MULTIMEDIA_CARD = "multimedia-card"
  val CLASS_MULTIMEDIA_CARD_LABEL = "multimedia-card-label"
  val CLASS_MULTIMEDIA_CARD_LABEL_UNAVAILABLE = "multimedia-card-label-unavailable"
  val CLASS_MULTIMEDIA_CARD_IMAGE = "multimedia-card-image"
  val CLASS_MULTIMEDIA_CARD_IMAGE_CENTER_OVER = "multimedia-card-image-center-over"
  val CLASS_MULTIMEDIA_CARD_IMAGE_TOP_OVER = "multimedia-card-image-top-over"
  val CLASS_MULTIMEDIA_CARD_IMAGE_UNDER = "multimedia-card-image-under"
  val CLASS_MULTIMEDIA_CARD_SUBLABEL = "multimedia-card-sublabel"

  val BASE_OVERLAY_URL = "/asset/image/overlay/"
  val MEDIA_SERVICE_LOGO_ALT = "media service logo"
  val MEDIA_IMAGE_ALT = "media image"

}
