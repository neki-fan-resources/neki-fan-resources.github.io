package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.Url
import org.skyluc.html.*

import Html.*

object Head {

  private val CHARSET_UTF8 = "utf-8"
  val charsetUtf8 = Seq(meta().withCharset(CHARSET_UTF8))

  // ----------

  private val REL_PRECONNECT = "preconnect"
  private val HREF_GOOGLE_FONTS_1 = "https://fonts.googleapis.com"
  private val HREF_GOOGLE_FONTS_2 = "https://fonts.gstatic.com"

  def googleFonts(href_font: String) = Seq(
    link(REL_PRECONNECT, HREF_GOOGLE_FONTS_1),
    link(REL_PRECONNECT, HREF_GOOGLE_FONTS_2).withCrossorigin(true),
    link(REL_STYLESHEET, href_font),
  )

  // ------------

  def css(urls: Url*) = urls.map { u =>
    link(REL_STYLESHEET, u.toString)
  }

  // ------------
  def icons(icon512: Url) = Seq(
    link(REL_ICON, HREF_FAVICON),
    link(REL_ICON, icon512.toString()).withType(TYPE_PNG).withSizes(SIZES_512),
  )
  private val TYPE_PNG = "image/png"
  private val HREF_FAVICON = "/favicon.ico"
  private val SIZES_512 = "512x512"
  private val REL_ICON = "icon"

  // ------------
  def statistics(isLocal: Boolean, dataDomain: String) = if (isLocal) {
    List(
      meta().withName(NAME_LOCAL).withContent(isLocal.toString())
    )
  } else {
    List(
      script().withDataDomain(dataDomain).withSrc(SRC_PLAUSIBLE).withDefer(true)
    )
  }
  private val NAME_LOCAL = "local"
  private val SRC_PLAUSIBLE = "https://plausible.io/js/script.hash.outbound-links.tagged-events.js"

  // -------------
  def searchEngineVerification(googleVerificationCode: String, microsoftVerificationCode: String) = Seq(
    meta().withName(NAME_GOOGLE_VERIFICATION).withContent(googleVerificationCode),
    meta().withName(NAME_MICROSOFT_VERIFICATION).withContent(microsoftVerificationCode),
  )
  val NAME_GOOGLE_VERIFICATION = "google-site-verification"
  val NAME_MICROSOFT_VERIFICATION = "msvalidate.01"

  // ==================

  private val REL_STYLESHEET = "stylesheet"
}
