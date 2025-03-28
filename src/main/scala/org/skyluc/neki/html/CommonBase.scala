package org.skyluc.neki.html

import org.skyluc.html.Html
import org.skyluc.html.Head
import org.skyluc.html.HeadElement
import org.skyluc.html.HtmlTag
import org.skyluc.neki.Config
import org.skyluc.html.Body
import org.skyluc.html.Html.html
import org.skyluc.html.BodyElement

object CommonBase {

  def generate(page: Page): Html = {

    Html
      .html()
      .withHead(
        head(page)
      )
      .withBody(
        body(page)
      )
  }

  private def head(page: Page): Head = {
    val elements =
      Html.meta().withCharset(CHARSET_UTF8) ::
        headFonts() :::
        searchEngineVerification(page) :::
        css() :::
        icons() :::
        opengraph(page) :::
        statistics()

    Html.head().appendElements(elements*)
  }

  private def headFonts(): List[HeadElement[?]] = {
    List(
      Html.link(REL_PRECONNECT, HREF_GOOGLE_FONTS_1),
      Html.link(REL_PRECONNECT, HREF_GOOGLE_FONTS_2).withCrossorigin(true),
      Html.link(REL_STYLESHEET, HREF_GOOGLE_FONT_NOTO),
    )
  }

  private def searchEngineVerification(page: Page): List[HeadElement[?]] =
    if (page.isRoot) {
      List(
        Html.meta().withName(NAME_GOOGLE_VERIFICATION).withContent(CONTENT_GOOGLE_VERIFICATION),
        Html.meta().withName(NAME_MICROSOFT_VERIFICATION).withContent(CONTENT_MICROSOFT_VERIFICATION),
      )
    } else {
      Nil
    }
  
  private def css(): List[HeadElement[?]] =
    List(
      Html.link(REL_STYLESHEET, HREF_STYLESHEET)
    )

  private def icons(): List[HeadElement[?]] =
    List(
      Html.link(REL_ICON, HREF_FAVICON),
      Html.link(REL_ICON, HREF_PNG_512).withType(TYPE_PNG).withSizes(SIZES_512)
    )

  private def opengraph(page: Page): List[HeadElement[?]] = {
      // TODO:
      val title = page.shortTitle() + COMMON_TITLE
      val descriptionAltName = page.altName().map(n => s" $n.").getOrElse(EMPTY)
      val description = s"$title$COMMON_DESCRIPTION$descriptionAltName"
      val canonicalUrl = Config.baseUrl + page.getPath().toString()
      List(
        Html.link(REL_CANONICAL, canonicalUrl),
        Html.title(title),
        Html.meta().withName(NAME_DESCRIPTION).withContent(description),
        Html.meta().withProperty(PROPERTY_OG_TITLE).withContent(title),
        Html.meta().withProperty(PROPERTY_OG_DESCRIPTION).withContent(description),
        // TODO: og image
        Html.meta().withProperty(PROPERTY_OG_TYPE).withContent(CONTENT_OG_TYPE),
        Html.meta().withProperty(PROPERTY_OG_URL).withContent(canonicalUrl),
        Html.meta().withProperty(PROPERTY_OG_LOGO).withContent(CONTENT_OG_LOGO),
        Html.meta().withProperty(PROPERTY_OG_LOCALE).withContent(CONTENT_OG_LOCALE)
      )
    }

  private def statistics(): List[HeadElement[?]] =
    if (Config.isLocal) {
      List(
        Html.meta().withName(NAME_LOCAL).withContent(Config.isLocal.toString())
      )
    } else {
      List(
        Html.script().withDataDomain(DATA_DOMAIN_NIFR).withSrc(SRC_PLAUSIBLE).withDefer(true)
      )
    }
  
  private def body(page: Page): Body = {
    Html.body().appendElements(
      Html.script().withSrc(SRC_JAVASCRIPT)
    ).appendElements(
      mainLayout(page)*
    )
  }

  private def mainLayout(page: Page): List[BodyElement[?]] = {
    List(
      Html.div(MAIN_HEADER),
      Html.div(MAIN_MAIN),
      Html.div(MAIN_FOOTER),
    )
  }

  // -------------

  final val EMPTY = ""
  final val SEPARATOR = " - "

  final val CHARSET_UTF8 = "utf-8"

  final val REL_PRECONNECT = "preconnect"
  final val REL_STYLESHEET = "stylesheet"
  final val REL_ICON = "icon"

  // fonts
  final val HREF_GOOGLE_FONTS_1 = "https://fonts.googleapis.com"
  final val HREF_GOOGLE_FONTS_2 = "https://fonts.gstatic.com"
  final val HREF_GOOGLE_FONT_NOTO = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap"

  // authentication
  final val NAME_GOOGLE_VERIFICATION = "google-site-verification"
  final val CONTENT_GOOGLE_VERIFICATION = "DrE-ZbcyBV3lPatFCBja2O4ymKzfqFXDZjkfkTpvY_8"
  final val NAME_MICROSOFT_VERIFICATION = "msvalidate.01"
  final val CONTENT_MICROSOFT_VERIFICATION = "B6C2BBE1BBDED01F740330EB10DEAEF8"

  // stylesheet
  final val HREF_STYLESHEET = "/asset/css/styles.css"

  // icons
  final val TYPE_PNG = "image/png"
  final val HREF_FAVICON = "/favicon.ico"
  final val HREF_PNG_512 = "/manekineko-512px.png"
  final val SIZES_512 = "512x512"

  // statistics
  final val NAME_LOCAL = "local"
  final val DATA_DOMAIN_NIFR = "neki-fan-resources.github.io"
  final val SRC_PLAUSIBLE = "https://plausible.io/js/script.outbound-links.tagged-events.js"

  // javascript
  final val SRC_JAVASCRIPT = "/asset/javascript/main.js"

  // opengraph
  final val COMMON_TITLE = " - NEK! - NEK! (NEKI) Fan Resources"
  final val COMMON_DESCRIPTION = ". NEK! (NEKI) fan website. Provides resources around the band NEK!. Lyrics, videos, live, concerts, history."

  final val REL_CANONICAL = "canonical"
  final val NAME_DESCRIPTION = "description"

  final val PROPERTY_OG_TITLE = "og:title"
  final val PROPERTY_OG_DESCRIPTION = "og:description"
  final val PROPERTY_OG_IMAGE = "og:image"
  final val PROPERTY_OG_TYPE = "og:type"
  final val PROPERTY_OG_URL = "og:url"
  final val PROPERTY_OG_LOGO = "og:logo"
  final val PROPERTY_OG_LOCALE = "og:locale"

  final val CONTENT_OG_TYPE = "website"
  final val CONTENT_OG_LOGO = Config.baseUrl + "manekineko-512px.png"
  final val CONTENT_OG_LOCALE = "en_US"

  // main layout
  final val MAIN_HEADER = "header"
  final val MAIN_MAIN = "main"
  final val MAIN_FOOTER = "footer"
}
