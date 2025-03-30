package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object LineCard {

  def generate(data: ItemCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(img().withClass(CLASS_LINE_CARD_COVER).withSrc(data.coverUrl).withAlt(data.coverAlt)),
      Some(
        div()
          .withClass(CLASS_LINE_CARD_DESIGNATION)
          .appendElements(
            text(data.designation)
          )
      ),
      data.date.map { date =>
        div().withClass(CLASS_LINE_CARD_DATE).appendElements(text(date))
      },
      Some(
        div()
          .withClass(CLASS_LINE_CARD_LABEL)
          .appendElements(
            text(data.label)
          )
      ),
    ).flatten

    a()
      .withClass(CLASS_LINE_CARD)
      .withHref(data.url)
      .appendElements(
        elements*
      )
  }

  // ------------

  val CLASS_LINE_CARD = "line-card"
  val CLASS_LINE_CARD_COVER = "line-card-cover"
  val CLASS_LINE_CARD_DESIGNATION = "line-card-designation"
  val CLASS_LINE_CARD_DATE = "line-card-date"
  val CLASS_LINE_CARD_LABEL = "line-card-label"

}
