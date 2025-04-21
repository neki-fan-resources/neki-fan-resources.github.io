package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.html.*

import Html.*

object LineCard {

  private val CLASS_LINE_CARD = "line-card"
  private val CLASS_LINE_CARD_DESIGNATION = "line-card-designation"
  private val CLASS_LINE_CARD_DATE = "line-card-date"
  private val CLASS_LINE_CARD_LABEL = "line-card-label"

  def generate(compiledData: ElementCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(SmallImageCover.generate(compiledData.cover)),
      Some(
        div()
          .withClass(CLASS_LINE_CARD_DESIGNATION)
          .appendElements(
            text(compiledData.designation)
          )
      ),
      compiledData.displayDate.map { date =>
        div().withClass(CLASS_LINE_CARD_DATE).appendElements(text(date))
      },
      Some(
        div()
          .withClass(CLASS_LINE_CARD_LABEL)
          .appendElements(
            text(compiledData.label)
          )
      ),
    ).flatten

    a()
      .withClass(CLASS_LINE_CARD)
      .withHref(compiledData.targetUrl.toString)
      .appendElements(
        elements*
      )
  }

}
