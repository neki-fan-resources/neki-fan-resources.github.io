package org.skyluc.neki.html

import org.skyluc.html.Html
import org.skyluc.html.Html._
import org.skyluc.html.Head
import org.skyluc.html.HeadElement
import org.skyluc.neki.Config
import org.skyluc.html.Body
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
      meta().withCharset(CHARSET_UTF8) ::
        headFonts() :::
        searchEngineVerification(page) :::
        css(page.isDark) :::
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

  private def css(dark: Boolean): List[HeadElement[?]] =
    if (dark) {
      List(
        link(REL_STYLESHEET, HREF_STYLESHEET),
        link(REL_STYLESHEET, HREF_STYLESHEET_DARK),
      )
    } else {
      List(
        link(REL_STYLESHEET, HREF_STYLESHEET)
      )
    }

  private def icons(): List[HeadElement[?]] =
    List(
      link(REL_ICON, HREF_FAVICON),
      link(REL_ICON, HREF_PNG_512).withType(TYPE_PNG).withSizes(SIZES_512),
    )

  private def opengraph(page: Page): List[HeadElement[?]] = {
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
      meta().withProperty(PROPERTY_OG_LOCALE).withContent(CONTENT_OG_LOCALE),
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
    Html
      .body()
      .appendElements(
        script().withSrc(SRC_JAVASCRIPT)
      )
      .appendElements(
        pageLayout(page)*
      )
  }

  private def pageLayout(page: Page): List[BodyElement[?]] = {
    List(
      div(PAGE_HEADER)
        .appendElements(navBar(page)*),
      div(PAGE_MAIN)
        .appendElements(
          div()
            .withId(MAIN_CONTENT)
            .appendElements(h1().appendElement(text(page.shortTitle())))
            .appendElements(page.mainContent()*)
        ),
      div(PAGE_FOOTER)
        .appendElements(footer(page)*),
      div()
        .withId(MAIN_OVERLAY)
        .withClass(CLASS_MAIN_OVERLAY_HIDDEN)
        .appendElements(
          div().withClass(CLASS_MAIN_OVERLAY_CLOSE).appendElements(text("⨯")).withOnClick("closeOverlay()"),
          div().withId(MAIN_OVERLAY_CONTENT),
        ),
    )
  }

  private def navBar(page: Page): List[BodyElement[?]] = {
    List(
      a()
        .withHref(ROOT_PATH)
        .withClass(CLASS_NAV_LOGO)
        .appendElements(
          img()
            .withClass(CLASS_NAV_LOGO_IMG)
            .withSrc(NAV_LOGO_PATH)
            .withAlt(NAV_LOGO_ALT)
        ),
      div(NAV_DIV).appendElements(
        a()
          .withClass(CLASS_NAV_SITE_TITLE)
          .withHref(ROOT_PATH)
          .appendElements(
            text(NAV_TITLE_TEXT)
          ),
        div()
          .withClass(CLASS_NAV_MAIN_ITEMS)
          .appendElements(
            page.data.site.navigation.main.map { item =>
              val element = a()
                .withClass(CLASS_NAV_MAIN_ITEM)
                .withHref(item.link)
                .appendElements(
                  text(item.name)
                )
              if (item.highlight.contains(page.path().getName(0).toString())) {
                element.withClass(CLASS_NAV_ITEM_SELECTED)
              } else {
                element
              }
            }*
          ),
        div()
          .withClass(CLASS_NAV_SUPPORT_ITEMS)
          .appendElements(
            page.data.site.navigation.support.map { item =>
              a()
                .withClass(CLASS_NAV_SUPPORT_ITEM)
                .withHref(item.link)
                .appendElements(
                  text(item.name)
                )
            }*
          ),
      ),
    )
  }

  private def footer(page: Page): List[BodyElement[?]] = {
    page.oppositePage.map { oppositeUrl =>
      a()
        .withHref(oppositeUrl)
        .withClass(CLASS_FOOTER_BOTTOM_LEFT)
        .appendElements(
          text("π")
        )
    }.toList :::
      List(
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(FOOTER_TEXT_1)
          ),
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(FOOTER_TEXT_2)
          ),
        div()
          .withClass(CLASS_FOOTER_CONTENT)
          .appendElements(
            text(FOOTER_TEXT_3)
          ),
        a()
          .withClass(CLASS_FOOTER_BOTTOM_RIGHT)
          .withHref(ABOUT_PATH)
          .appendElements(
            text(FOOTER_TEXT_4)
          ),
      )
  }

  // -------------

  val EMPTY = ""
  val SEPARATOR = " - "

  val BLANK = "_blank"

  val CHARSET_UTF8 = "utf-8"

  val REL_PRECONNECT = "preconnect"
  val REL_STYLESHEET = "stylesheet"
  val REL_ICON = "icon"

  // fonts
  val HREF_GOOGLE_FONTS_1 = "https://fonts.googleapis.com"
  val HREF_GOOGLE_FONTS_2 = "https://fonts.gstatic.com"
  val HREF_GOOGLE_FONT_NOTO = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap"

  // authentication
  val NAME_GOOGLE_VERIFICATION = "google-site-verification"
  val CONTENT_GOOGLE_VERIFICATION = "DrE-ZbcyBV3lPatFCBja2O4ymKzfqFXDZjkfkTpvY_8"
  val NAME_MICROSOFT_VERIFICATION = "msvalidate.01"
  val CONTENT_MICROSOFT_VERIFICATION = "B6C2BBE1BBDED01F740330EB10DEAEF8"

  // stylesheet
  val HREF_STYLESHEET = "/asset/css/styles.css"
  val HREF_STYLESHEET_DARK = "/asset/css/dark.css"

  // icons
  val TYPE_PNG = "image/png"
  val HREF_FAVICON = "/favicon.ico"
  val HREF_PNG_512 = "/manekineko-512px.png"
  val SIZES_512 = "512x512"

  // statistics
  val NAME_LOCAL = "local"
  val DATA_DOMAIN_NIFR = "neki-fan-resources.github.io"
  val SRC_PLAUSIBLE = "https://plausible.io/js/script.outbound-links.tagged-events.js"

  // javascript
  val SRC_JAVASCRIPT = "/asset/javascript/main.js"

  // opengraph
  val COMMON_TITLE = " - NEK! - NEK! (NEKI) Fan Resources"
  val COMMON_DESCRIPTION =
    ". NEK! (NEKI) fan website. Provides resources around the band NEK!. Lyrics, videos, live, concerts, history."

  val REL_CANONICAL = "canonical"
  val NAME_DESCRIPTION = "description"

  val PROPERTY_OG_TITLE = "og:title"
  val PROPERTY_OG_DESCRIPTION = "og:description"
  val PROPERTY_OG_IMAGE = "og:image"
  val PROPERTY_OG_TYPE = "og:type"
  val PROPERTY_OG_URL = "og:url"
  val PROPERTY_OG_LOGO = "og:logo"
  val PROPERTY_OG_LOCALE = "og:locale"

  val CONTENT_OG_TYPE = "website"
  val CONTENT_OG_LOGO = Config.baseUrl + "manekineko-512px.png"
  val CONTENT_OG_LOCALE = "en_US"

  // page layout
  val PAGE_HEADER = "header"
  val PAGE_MAIN = "main"
  val PAGE_FOOTER = "footer"

  // main layout
  val MAIN_OVERLAY = "main-overlay"
  val MAIN_OVERLAY_CONTENT = "main-overlay-content"
  val MAIN_SCROLLABLE = "main-scrollable"
  val MAIN_BACKGROUND = "main-background"
  val MAIN_CONTENT = "main-content"
  val CLASS_MAIN_TITLE = "main-title"
  val CLASS_MAIN_OVERLAY_HIDDEN = "main-overlay-hidden"
  val CLASS_MAIN_OVERLAY_CLOSE = "main-overlay-close"

  // navbar
  val NAV_DIV = "nav"

  val NAV_LOGO_PATH = "/asset/image/site/manekineko-200px.png"
  val NAV_LOGO_ALT = "N!fr manekineko logo"
  val NAV_TITLE_TEXT = "NEK!<br>Fan<br>Resources"

  val CLASS_NAV_LOGO = "nav-logo"
  val CLASS_NAV_LOGO_IMG = "nav-logo-img"
  val CLASS_NAV_SITE_TITLE = "nav-site-title"
  val CLASS_NAV_MAIN_ITEMS = "nav-main-items"
  val CLASS_NAV_MAIN_ITEM = "nav-main-item"
  val CLASS_NAV_SUPPORT_ITEMS = "nav-support-items"
  val CLASS_NAV_SUPPORT_ITEM = "nav-support-item"
  val CLASS_NAV_ITEM_SELECTED = "nav-item-selected"

  // footer
  val CLASS_FOOTER_CONTENT = "footer-content"
  val CLASS_FOOTER_BOTTOM_RIGHT = "footer-bottom-right"
  val CLASS_FOOTER_BOTTOM_LEFT = "footer-bottom-left"

  val FOOTER_TEXT_1 = "This website is not associated with the band NEK! or their production team."
  val FOOTER_TEXT_2 =
    "© Original content, website structure: SkyLuc. Lyrics, band resources, external resources: their respective owners."
  val FOOTER_TEXT_3 =
    "We aim to provide information as accurate as possible. If you notice a problem, please contact us."
  val FOOTER_TEXT_4 = "questions and requests"

  // TODO: replaced with computed (once) values
  val ROOT_PATH = "/"
  val DARK_PATH = "dark"
  val ABOUT_PATH = "/about.html#questions"
}
