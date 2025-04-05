package org.skyluc.neki.html

import org.skyluc.html._
import Html._

object SectionHeader {
  def generate(title: String): H2 = {
    h2().appendElements(text(title))
  }

  def generate(content: BodyElement[?]*): H2 = {
    h2().appendElements(content*)
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

object ExtraLink {
  def generate(link: String): Div = {
    div()
      .withClass(CLASS_MAIN_EXTRA)
      .appendElements(
        a().withHref(link).appendElements(text(EXTRA_TEXT))
      )
  }

  // ----

  val EXTRA_TEXT = "EXTRA →"

  val CLASS_MAIN_EXTRA = "main-extra"
}
