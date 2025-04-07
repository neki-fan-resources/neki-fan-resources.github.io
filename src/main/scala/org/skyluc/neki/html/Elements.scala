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

object SocialMediaCard {

  // TODO: real support

  def generate(
      handle: String,
      baseUrl: String,
      service: String,
      logoFilename: String,
      link: Boolean = false,
  ): BodyElement[?] = {
    a()
      .withHref(baseUrl + handle)
      .withTarget(CommonBase.BLANK)
      .withClass(if (link) CLASS_SOCIALMEDIA_CARD_LINK else CLASS_SOCIALMEDIA_CARD)
      .appendElements(
        img()
          .withClass(CLASS_SOCIALMEDIA_CARD_LOGO)
          .withSrc(URL_LOGO_BASE + logoFilename)
          .withAlt(service + SOCIALMEDIA_LOGO_ALT),
        text(handle),
      )
  }

  // -----

  val CLASS_SOCIALMEDIA_CARD = "socialmedia-card"
  val CLASS_SOCIALMEDIA_CARD_LINK = "socialmedia-card-link"
  val CLASS_SOCIALMEDIA_CARD_LOGO = "socialmedia-card-logo"

  val URL_LOGO_BASE = "/asset/image/logo/"
  val SOCIALMEDIA_LOGO_ALT = " logo"
}
