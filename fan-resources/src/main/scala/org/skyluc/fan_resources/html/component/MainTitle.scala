package org.skyluc.fan_resources.html.component

import org.skyluc.html.*

import Html.*

object MainTitle {

  private val CLASS_MAIN_TITLE = "main-title"

  def generate(elements: BodyElement[?]*): Div = {
    div()
      .withClass(CLASS_MAIN_TITLE)
      .appendElements(
        elements*
      )
  }
}
