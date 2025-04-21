package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.Common
import org.skyluc.html.*

import Html.*

object SocialMediaCard {

  private val CLASS_SOCIALMEDIA_CARD = "socialmedia-card"
  private val CLASS_SOCIALMEDIA_CARD_LINK = "socialmedia-card-link"
  private val CLASS_SOCIALMEDIA_CARD_LOGO = "socialmedia-card-logo"

  private val URL_LOGO_BASE = "/asset/image/logo/"
  private val SOCIALMEDIA_LOGO_ALT = " logo"

  // -----

  // TODO: real support

  def generate(
      handle: String,
      baseUrl: String,
      service: String,
      logoFilename: String,
      link: Boolean = false,
  ): A = {
    a()
      .withHref(baseUrl + handle)
      .withTarget(Common.BLANK)
      .withClass(if (link) CLASS_SOCIALMEDIA_CARD_LINK else CLASS_SOCIALMEDIA_CARD)
      .appendElements(
        img()
          .withClass(CLASS_SOCIALMEDIA_CARD_LOGO)
          .withSrc(URL_LOGO_BASE + logoFilename)
          .withAlt(service + SOCIALMEDIA_LOGO_ALT),
        text(handle),
      )
  }
}
