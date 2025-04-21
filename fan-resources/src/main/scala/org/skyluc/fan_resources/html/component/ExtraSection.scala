package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.Url
import org.skyluc.html.*

import Html.*

object ExtraSection {

  private val EXTRA_TEXT = "EXTRA →"

  private val CLASS_MAIN_EXTRA = "main-extra"

  // -------

  def generate(extraUrl: Url): Seq[BodyElement[?]] = {
    Seq(
      div()
        .withClass(CLASS_MAIN_EXTRA)
        .appendElements(
          a().withHref(extraUrl.toString()).appendElements(text(EXTRA_TEXT))
        )
    )
  }
}
