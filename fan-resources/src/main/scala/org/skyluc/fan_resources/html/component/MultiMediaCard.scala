package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.*
import org.skyluc.html.*

import Html.*

object MultiMediaCard {

  //
  private val CLASS_MULTIMEDIA_CARD_LIST = "multimedia-card-list"
  private val CLASS_MULTIMEDIA_CARD = "multimedia-card"
  private val CLASS_MULTIMEDIA_CARD_LABEL = "multimedia-card-label"
  private val CLASS_MULTIMEDIA_CARD_LABEL_UNAVAILABLE = "multimedia-card-label-unavailable"
  private val CLASS_MULTIMEDIA_CARD_SUBLABEL = "multimedia-card-sublabel"

  // section titles
  private val SECTION_CONCERT_TEXT = "Concert"
  private val SECTION_VIDEO_TEXT = "Video"
  private val SECTION_SHORT_TEXT = "Short"
  private val SECTION_ADDITIONAL_TEXT = "Additional Media"
  private val SECTION_MEDIA_TEXT = "Media"
  private val SECTION_IMAGE_TEXT = "Image"
  private val SECTION_LIVE_TEXT = "Live"

  def generate(multimedia: MultiMediaCompiledData, fromKey: String): Div = {
    val infoParts =
      List(
        multimedia.froms.filterNot(_._1 == fromKey).map(_._2.label),
        multimedia.info,
      ).flatten

    val label1 = div().withClass(CLASS_MULTIMEDIA_CARD_LABEL).appendElements(text(multimedia.label))
    val label2 = if (multimedia.available) {
      label1
    } else {
      label1.withClass(CLASS_MULTIMEDIA_CARD_LABEL_UNAVAILABLE)
    }
    val elements: List[BodyElement[?]] = List(
      label2,
      MultiMediaImage.generate(multimedia.image, multimedia.targetUrl),
      div().withClass(CLASS_MULTIMEDIA_CARD_SUBLABEL).appendElements(text(infoParts.mkString(", "))),
    )
    div()
      .withClass(CLASS_MULTIMEDIA_CARD)
      .appendElements(
        elements*
      )
  }

  def generateList(multimedias: Seq[MultiMediaCompiledData], fromKey: String): Div = {
    div()
      .withClass(CLASS_MULTIMEDIA_CARD_LIST)
      .appendElements(
        multimedias.map(generate(_, fromKey))*
      )
  }

  def generateSection(
      sectionLabel: String,
      multimedias: Seq[MultiMediaCompiledData],
      fromFilter: String,
  ): Seq[BodyElement[?]] = {
    if (multimedias.isEmpty) {
      Seq()
    } else {
      Seq(
        SectionHeader.generate(sectionLabel),
        generateList(multimedias, fromFilter),
      )
    }
  }

  def generateMainSections(multimediaBlock: MultiMediaBlockCompiledData, fromFilter: String): Seq[BodyElement[?]] = {
    val concertSection = generateSection(
      SECTION_CONCERT_TEXT,
      multimediaBlock.concert,
      fromFilter,
    )

    val videoSection = generateSection(
      SECTION_VIDEO_TEXT,
      multimediaBlock.video,
      fromFilter,
    )

    val liveSection = generateSection(
      SECTION_LIVE_TEXT,
      multimediaBlock.live,
      fromFilter,
    )

    val shortSection = generateSection(
      SECTION_SHORT_TEXT,
      multimediaBlock.short,
      fromFilter,
    )

    val imageSection = generateSection(
      SECTION_IMAGE_TEXT,
      multimediaBlock.image,
      fromFilter,
    )

    concertSection ++ videoSection ++ liveSection ++ shortSection ++ imageSection
  }

  def generateAdditionalSection(
      multimediaBlock: MultiMediaBlockCompiledData,
      fromFilter: String,
  ): Seq[BodyElement[?]] = {
    generateSection(SECTION_ADDITIONAL_TEXT, multimediaBlock.additional, fromFilter)
  }

  def generateExtraMediaSection(
      multimediaBlock: MultiMediaBlockCompiledData,
      fromFilter: String,
  ): Seq[BodyElement[?]] = {
    generateSection(SECTION_MEDIA_TEXT, multimediaBlock.extra.sortBy(_.date).reverse, fromFilter)
  }
}

object MultiMediaImage {
  private val CLASS_MULTIMEDIA_CARD_IMAGE = "multimedia-card-image"
  private val CLASS_MULTIMEDIA_CARD_IMAGE_CENTER_OVER = "multimedia-card-image-center-over"
  private val CLASS_MULTIMEDIA_CARD_IMAGE_TOP_OVER = "multimedia-card-image-top-over"
  private val CLASS_MULTIMEDIA_CARD_IMAGE_UNDER = "multimedia-card-image-under"

  def generate(image: ImageWithOverlayCompiledData, targetUrl: Url): BodyElement[?] = {
    a()
      .withClass(CLASS_MULTIMEDIA_CARD_IMAGE)
      .withHref(targetUrl.toString)
      .withTarget(A.TARGET_BLANK)
      .appendElements(
        img()
          .withClass(
            if (image.centerOverlay) CLASS_MULTIMEDIA_CARD_IMAGE_CENTER_OVER else CLASS_MULTIMEDIA_CARD_IMAGE_TOP_OVER
          )
          .withSrc(image.overlaySource.toString())
          .withAlt(image.overlayAlt),
        img()
          .withClass(CLASS_MULTIMEDIA_CARD_IMAGE_UNDER)
          .withSrc(image.source.toString())
          .withAlt(image.alt),
      )
  }
}
