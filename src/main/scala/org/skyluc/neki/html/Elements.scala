package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object SectionHeader {
  def generate(title: String): H2 = {
    h2().appendElement(text(title))
  }
}

object MainIntro {
  def generate(content: String): Div = {
    div().withClass(CLASS_MAIN_INTRO).appendElements(text(content))
  }

  def generate(content: List[BodyElement[?]]): Div = {
    div().withClass(CLASS_MAIN_INTRO).appendElements(content*)
  }

  // -----

  val CLASS_MAIN_INTRO = "main-intro"
}
