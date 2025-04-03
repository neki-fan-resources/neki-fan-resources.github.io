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

  def generate(entry: MultiMediaCompiledData, fromFilter: String): Div = {
    val infoParts =
      List(
        entry.from.filterNot(_._1 == fromFilter).map(_._2.label),
        entry.info,
      ).flatten
    val elements: List[BodyElement[?]] = List(
      div().withClass(CLASS_MULTIMEDIA_CARD_LABEL).appendElements(text(entry.label)),
      a()
        .withHref(entry.url)
        .withTarget(A.TARGET_BLANK)
        .appendElements(
          div()
            .withClass(CLASS_MULTIMEDIA_CARD_IMAGE)
            .appendElements(
              img()
                .withClass(CLASS_MULTIMEDIA_CARD_IMAGE_OVER)
                .withSrc(BASE_OVERLAY_URL + entry.overlay)
                .withAlt(MEDIA_SERVICE_LOGO_ALT),
              img()
                .withClass(CLASS_MULTIMEDIA_CARD_IMAGE_UNDER)
                .withSrc(entry.imageUrl)
                .withAlt(entry.label + MEDIA_IMAGE_ALT),
            )
        ),
      div().withClass(CLASS_MULTIMEDIA_CARD_SUBLABEL).appendElements(text(infoParts.mkString(", "))),
    )
    div()
      .withClass(CLASS_MULTIMEDIA_CARD)
      .appendElements(
        elements*
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
  val CLASS_MULTIMEDIA_CARD_IMAGE = "multimedia-card-image"
  val CLASS_MULTIMEDIA_CARD_IMAGE_OVER = "multimedia-card-image-over"
  val CLASS_MULTIMEDIA_CARD_IMAGE_UNDER = "multimedia-card-image-under"
  val CLASS_MULTIMEDIA_CARD_SUBLABEL = "multimedia-card-sublabel"

  val BASE_OVERLAY_URL = "/asset/image/overlay/"
  val MEDIA_SERVICE_LOGO_ALT = "media service logo"
  val MEDIA_IMAGE_ALT = "media image"

}
