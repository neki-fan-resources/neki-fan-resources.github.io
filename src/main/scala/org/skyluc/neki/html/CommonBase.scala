package org.skyluc.neki.html

import org.skyluc.html.Html
import org.skyluc.html.Html._
import org.skyluc.html.Head
import org.skyluc.html.HeadElement
import org.skyluc.html.HtmlTag
import org.skyluc.neki.Config
import org.skyluc.html.Body
import org.skyluc.html.BodyElement
import org.skyluc.html.Div

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
      meta().withCharset(CHARSET_UTF8) ::
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
      link(REL_PRECONNECT, HREF_GOOGLE_FONTS_1),
      link(REL_PRECONNECT, HREF_GOOGLE_FONTS_2).withCrossorigin(true),
      link(REL_STYLESHEET, HREF_GOOGLE_FONT_NOTO),
    )
  }

  private def searchEngineVerification(page: Page): List[HeadElement[?]] =
    if (page.isRoot) {
      List(
        meta().withName(NAME_GOOGLE_VERIFICATION).withContent(CONTENT_GOOGLE_VERIFICATION),
        meta().withName(NAME_MICROSOFT_VERIFICATION).withContent(CONTENT_MICROSOFT_VERIFICATION),
      )
    } else {
      Nil
    }
  
  private def css(): List[HeadElement[?]] =
    List(
      link(REL_STYLESHEET, HREF_STYLESHEET)
    )

  private def icons(): List[HeadElement[?]] =
    List(
      link(REL_ICON, HREF_FAVICON),
      link(REL_ICON, HREF_PNG_512).withType(TYPE_PNG).withSizes(SIZES_512)
    )

  private def opengraph(page: Page): List[HeadElement[?]] = {
      // TODO:
      val title = page.shortTitle() + COMMON_TITLE
      val descriptionAltName = page.altName().map(n => s" $n.").getOrElse(EMPTY)
      val description = s"$title$COMMON_DESCRIPTION$descriptionAltName"
      val canonicalUrl = Config.baseUrl + page.path().toString()
      List(
        link(REL_CANONICAL, canonicalUrl),
        Html.title(title),
        meta().withName(NAME_DESCRIPTION).withContent(description),
        meta().withProperty(PROPERTY_OG_TITLE).withContent(title),
        meta().withProperty(PROPERTY_OG_DESCRIPTION).withContent(description),
        // TODO: og image
        meta().withProperty(PROPERTY_OG_TYPE).withContent(CONTENT_OG_TYPE),
        meta().withProperty(PROPERTY_OG_URL).withContent(canonicalUrl),
        meta().withProperty(PROPERTY_OG_LOGO).withContent(CONTENT_OG_LOGO),
        meta().withProperty(PROPERTY_OG_LOCALE).withContent(CONTENT_OG_LOCALE)
      )
    }

  private def statistics(): List[HeadElement[?]] =
    if (Config.isLocal) {
      List(
        meta().withName(NAME_LOCAL).withContent(Config.isLocal.toString())
      )
    } else {
      List(
        script().withDataDomain(DATA_DOMAIN_NIFR).withSrc(SRC_PLAUSIBLE).withDefer(true)
      )
    }
  
  private def body(page: Page): Body = {
    Html.body().appendElements(
      script().withSrc(SRC_JAVASCRIPT)
    ).appendElements(
      pageLayout(page)*
    )
  }

  private def pageLayout(page: Page): List[BodyElement[?]] = {
    List(
      div(PAGE_HEADER)
        .appendElements(navBar(page)*),
      div(PAGE_MAIN)
        .appendElements(mainLayout(page)*),
      div(PAGE_FOOTER)
        .appendElements(footer(page)*),
    )
  }

  private def navBar(page: Page): List[BodyElement[?]] = {
    List(
      a().withHref(ROOT_PATH).withClass(CLASS_NAV_LOGO)
        .appendElements(
          img()
            .withClass(CLASS_NAV_LOGO_IMG)
            .withSrc(NAV_LOGO_PATH).withAlt(NAV_LOGO_ALT)
        ),
      div(NAV_DIV).appendElements(
        a().withClass(CLASS_NAV_SITE_TITLE).withHref(ROOT_PATH).appendElements(
          text(NAV_TITLE_TEXT)
        ),
        // TODO: support to underline currently visited section
        div().withClass(CLASS_NAV_MAIN_ITEMS).appendElements(
          page.data.site.navigation.main.map{ item =>
            a().withClass(CLASS_NAV_MAIN_ITEM).withHref(item.link).appendElements(
              text(item.name)
            )
          }*
        ),
        div().withClass(CLASS_NAV_SUPPORT_ITEMS).appendElements(
          page.data.site.navigation.support.map{ item =>
            a().withClass(CLASS_NAV_SUPPORT_ITEM).withHref(item.link).appendElements(
              text(item.name)
            )
          }*
        ),
      )
    )
  }

  private def mainLayout(page: Page): List[BodyElement[?]] = {
    List(
      div().withId(MAIN_OVERLAY).withClass(CLASS_MAIN_OVERLAY_HIDDEN),
      div().withId(MAIN_SCROLLABLE)
        .appendElements(
          div().withId(MAIN_BACKGROUND)
            .appendElements(
              div().withId(MAIN_CONTENT)
            )
        )
    )
  }

  private def footer(page: Page): List[BodyElement[?]] = {
    List(
      div().withClass(CLASS_FOOTER_CONTENT)
        .appendElements(
          text(FOOTER_TEXT_1)
        ),
      div().withClass(CLASS_FOOTER_CONTENT)
        .appendElements(
          text(FOOTER_TEXT_2)
        ),
      div().withClass(CLASS_FOOTER_CONTENT)
        .appendElements(
          text(FOOTER_TEXT_3)
        ),
      a().withClass(CLASS_FOOTER_BOTTOM_RIGHT).withHref(ABOUT_PATH)
        .appendElements(
          text(FOOTER_TEXT_4)
        ),
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

  // page layout
  final val PAGE_HEADER = "header"
  final val PAGE_MAIN = "main"
  final val PAGE_FOOTER = "footer"

  // main layout
  final val MAIN_OVERLAY = "main-overlay"
  final val MAIN_SCROLLABLE = "main-scrollable"
  final val MAIN_BACKGROUND = "main-background"
  final val MAIN_CONTENT = "main-content"
  final val CLASS_MAIN_OVERLAY_HIDDEN = "main-overlay-hidden"

  // navbar
  final val NAV_DIV = "nav"

  final val NAV_LOGO_PATH = "/asset/image/site/manekineko-200px.png"
  final val NAV_LOGO_ALT = "N!fr manekineko logo"
  final val NAV_TITLE_TEXT = "NEK!<br>Fan<br>Resources"

  final val CLASS_NAV_LOGO = "nav-logo"
  final val CLASS_NAV_LOGO_IMG = "nav-logo-img"
  final val CLASS_NAV_SITE_TITLE = "nav-site-title"
  final val CLASS_NAV_MAIN_ITEMS = "nav-main-items"
  final val CLASS_NAV_MAIN_ITEM = "nav-main-item"
  final val CLASS_NAV_SUPPORT_ITEMS = "nav-support-items"
  final val CLASS_NAV_SUPPORT_ITEM = "nav-support-item"

  // footer
  final val CLASS_FOOTER_CONTENT = "footer-content"
  final val CLASS_FOOTER_BOTTOM_RIGHT = "footer-bottom-right"
  final val CLASS_FOOTER_BOTTOM_LEFT = "footer-bottom-left"

  final val FOOTER_TEXT_1 = "This website is not associated with the band NEK! or their production team."
  final val FOOTER_TEXT_2 = "© Original content, website structure: SkyLuc. Lyrics, band resources, external resources: their respective owners."
  final val FOOTER_TEXT_3 = "We aim to provide information as accurate as possible. If you notice a problem, please contact us."
  final val FOOTER_TEXT_4 = "questions and requests"

  // TODO: replaced with computed (once) values
  final val ROOT_PATH = "/"
  final val ABOUT_PATH = "/about.html"
}
