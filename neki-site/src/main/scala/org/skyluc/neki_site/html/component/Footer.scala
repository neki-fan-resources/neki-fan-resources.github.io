package org.skyluc.neki_site.html.component

import org.skyluc.html._
import Html._
import org.skyluc.fan_resources.html.Url

object Footer {

  // classes
  val CLASS_FOOTER_CONTENT = "footer-content"
  val CLASS_FOOTER_BOTTOM_RIGHT = "footer-bottom-right"
  val CLASS_FOOTER_BOTTOM_LEFT = "footer-bottom-left"

  // text
  val TEXT_OPPOSITE_LINK = "π"
  val TEXT_FOOTER_1 = "This website is not associated with the band NEK! or their production team."
  val TEXT_FOOTER_2 =
    "© Original content, website structure: SkyLuc. Lyrics, band resources, external resources: their respective owners."
  val TEXT_FOOTER_3 =
    "We aim to provide information as accurate as possible. If you notice a problem, please contact us."
  val TEXT_FOOTER_4 = "questions and requests"

  // URLs
  // TODO: use computed values ?
  val ABOUT_PATH = "/about.html#questions"

  def generate(oppositeUrl: Option[Url]): Seq[BodyElement[?]] = {
    oppositeUrl.map { ou =>
      a()
        .withHref(ou.toString())
        .withClass(CLASS_FOOTER_BOTTOM_LEFT)
        .appendElements(
          text(TEXT_OPPOSITE_LINK)
        )
    }.toList :::
      List(
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(TEXT_FOOTER_1)
          ),
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(TEXT_FOOTER_2)
          ),
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(TEXT_FOOTER_3)
          ),
        a()
          .withClass(CLASS_FOOTER_BOTTOM_RIGHT)
          .withHref(ABOUT_PATH)
          .appendElements(
            text(TEXT_FOOTER_4)
          ),
      )
  }
}
