package org.skyluc.fan_resources.html.component

import org.skyluc.html.*

import Html.*

object MainIntro {
  def generate(content: String): Div = {
    div().withClass(CLASS_MAIN_INTRO).appendElements(text(content))
  }

  def generate(content: List[BodyElement[?]]): Div = {
    div().withClass(CLASS_MAIN_INTRO).appendElements(content*)
  }

  // -----

  private val CLASS_MAIN_INTRO = "main-intro"
}
