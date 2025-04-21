package org.skyluc.neki_site.html.component

import org.skyluc.html._
import Html._
import org.skyluc.neki_site.data.Navigation
import org.skyluc.fan_resources.data.Path

object NavigationBar {

  // div tag
  val NAV_DIV = "nav"

  // class
  val CLASS_NAV_LOGO = "nav-logo"
  val CLASS_NAV_LOGO_IMG = "nav-logo-img"
  val CLASS_NAV_SITE_TITLE = "nav-site-title"
  val CLASS_NAV_MAIN_ITEMS = "nav-main-items"
  val CLASS_NAV_MAIN_ITEM = "nav-main-item"
  val CLASS_NAV_SUPPORT_ITEMS = "nav-support-items"
  val CLASS_NAV_SUPPORT_ITEM = "nav-support-item"
  val CLASS_NAV_ITEM_SELECTED = "nav-item-selected"

  // text
  val NAV_LOGO_ALT = "N!fr manekineko logo"
  val NAV_TITLE_TEXT = "NEK!<br>Fan<br>Resources"

  // URLs
  val NAV_LOGO_PATH = "/asset/image/site/manekineko-200px.png"
  val ROOT_PATH = "/"

  // using outputPath to check which item to highlight is a hack. It would be better if using the
  // page URL
  def generate(navigation: Navigation, currentOutputPath: Path): Seq[BodyElement[?]] = {

    Seq(
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
            navigation.main.map { item =>
              val element = a()
                .withClass(CLASS_NAV_MAIN_ITEM)
                .withHref(item.link)
                .appendElements(
                  text(item.name)
                )
              if (item.highlight.contains(currentOutputPath.firstSegment())) {
                element.withClass(CLASS_NAV_ITEM_SELECTED)
              } else {
                element
              }
            }*
          ),
        div()
          .withClass(CLASS_NAV_SUPPORT_ITEMS)
          .appendElements(
            navigation.support.map { item =>
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
}
