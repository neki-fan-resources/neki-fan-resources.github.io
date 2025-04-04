package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object MediumDetails {

  def generate(label: String, coverUrl: String, coverAlt: String): Div = {
    val imgElement = img()
      .withClass(CLASS_MEDIUM_DETAILS_COVER_IMG)
      .withSrc(coverUrl)
      .withAlt(coverAlt)
    generate(
      Pages.TEXT_PLACEHOLDER,
      label,
      None,
      "bad URL", // TODO
      None,
      None,
      Nil,
      imgElement,
    )
  }

  def generate(data: ItemCompiledData): Div = {
    val imgElement = img()
      .withClass(CLASS_MEDIUM_DETAILS_COVER_IMG)
      .withSrc(data.coverUrl)
      .withAlt(data.coverAlt)
    generate(
      data.designation,
      data.label,
      data.sublabel,
      data.url,
      None,
      data.parent,
      data.info,
      imgElement,
    )
  }

  def generate(dataWithParentKey: MultiMediaCompiledDataWithParentKey): Div = {
    val data = dataWithParentKey.compiledData
    val imgElement = MultiMediaCard.generateLayeredImage(data)

    generate(
      data.designation,
      data.label,
      None,
      data.url,
      Some(CommonBase.BLANK),
      data.from.find(t => t._1 == dataWithParentKey.parentKey).map(_._2),
      List(ItemInfo("Published", data.date.toString(), ItemInfo.INFO_LEVEL_MINIMUM)),
      imgElement,
    )
  }

  def generate(
      designation: String,
      label: String,
      sublabel: Option[String],
      url: String,
      urlTarget: Option[String],
      parent: Option[ItemCompiledData],
      info: List[ItemInfo],
      img: BodyElement[?],
  ): Div = {

    val infoRows = info.filter(_.infoLevel <= ItemInfo.INFO_LEVEL_BASIC).map { info =>
      val value: BodyElement[?] =
        info.url
          .map { u =>
            a().withHref(u).withTarget(CommonBase.BLANK).appendElements(text(info.value))
          }
          .getOrElse(
            text(info.value)
          )
      tr().appendTds(
        td()
          .withClass(CLASS_MEDIUM_DETAILS_INFO_LABEL)
          .appendElement(
            text(info.label.getOrElse(CommonBase.EMPTY))
          ),
        td()
          .withClass(CLASS_MEDIUM_DETAILS_INFO_VALUE)
          .appendElement(
            value
          ),
      )
    }

    val labels = a()
      .withHref(url)
      .withTarget(urlTarget)
      .withClass(CLASS_MEDIUM_DETAILS_LABELS)
      .appendElements(div().withClass(CLASS_MEDIUM_DETAILS_LABEL).appendElements(text(label)))

    val elements: List[BodyElement[?]] = List(
      Some(
        a()
          .withHref(url)
          .withTarget(urlTarget)
          .withClass(CLASS_MEDIUM_DETAILS_COVER)
          .appendElements(img)
      ),
      Some(div().withClass(CLASS_MEDIUM_DETAILS_DESIGNATION).appendElements(text(designation))),
      // Some(a().withHref(url).withTarget(urlTarget).withClass(CLASS_MEDIUM_DETAILS_LABEL).appendElements(text(label))),
      sublabel
        .map(sublabel =>
          labels.appendElements(
            div()
              .withClass(CLASS_MEDIUM_DETAILS_SUBLABEL)
              .appendElements(text(sublabel))
          )
        )
        .orElse(Some(labels)),
      parent.map(p => LineCard.generate(p)),
      Some(
        div()
          .withClass(CLASS_MEDIUM_DETAILS_INFO)
          .appendElements(
            table().appendTbody(
              tbody().appendTrs(
                infoRows*
              )
            )
          )
      ),
    ).flatten

    div()
      .withClass(CLASS_MEDIUM_DETAILS)
      .appendElements(
        elements*
      )
  }

  // --------------------

  val CLASS_MEDIUM_DETAILS = "medium-details"
  val CLASS_MEDIUM_DETAILS_COVER = "medium-details-cover"
  val CLASS_MEDIUM_DETAILS_COVER_IMG = "medium-details-cover-img"
  val CLASS_MEDIUM_DETAILS_DESIGNATION = "medium-details-designation"
  val CLASS_MEDIUM_DETAILS_LABELS = "medium-details-labels"
  val CLASS_MEDIUM_DETAILS_LABEL = "medium-details-label"
  val CLASS_MEDIUM_DETAILS_SUBLABEL = "medium-details-sublabel"
  val CLASS_MEDIUM_DETAILS_INFO = "medium-details-info"
  val CLASS_MEDIUM_DETAILS_INFO_LABEL = "medium-details-info-label"
  val CLASS_MEDIUM_DETAILS_INFO_VALUE = "medium-details-info-value"
}
