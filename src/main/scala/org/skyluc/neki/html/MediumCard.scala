package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object MediumCard {

  def generateList(list: List[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        list.map(generate(_))*
      )
  }

  def generate(data: ItemCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(img().withClass(CLASS_MEDIUM_CARD_COVER).withSrc(data.coverUrl).withAlt(data.coverAlt)),
      Some(div().withClass(CLASS_MEDIUM_CARD_DESIGNATION).appendElements(text(data.designation))),
      Some(div().withClass(CLASS_MEDIUM_CARD_LABEL).appendElements(text(data.label))),
      data.sublabel.map(sublabel => (div().withClass(CLASS_MEDIUM_CARD_SUBLABEL).appendElements(text(sublabel)))),
      data.date.map(date => (div().withClass(CLASS_MEDIUM_CARD_DATE).appendElements(text(date)))),
    ).flatten

    a()
      .withClass(CLASS_MEDIUM_CARD)
      .withHref(data.url)
      .appendElements(
        elements*
      )
  }

  // --------------------

  val CLASS_MEDIUM_CARD_LIST = "medium-card-list"
  val CLASS_MEDIUM_CARD = "medium-card"
  val CLASS_MEDIUM_CARD_COVER = "medium-card-cover"
  val CLASS_MEDIUM_CARD_DESIGNATION = "medium-card-designation"
  val CLASS_MEDIUM_CARD_DATE = "medium-card-date"
  val CLASS_MEDIUM_CARD_LABEL = "medium-card-label"
  val CLASS_MEDIUM_CARD_SUBLABEL = "medium-card-sublabel"
}
