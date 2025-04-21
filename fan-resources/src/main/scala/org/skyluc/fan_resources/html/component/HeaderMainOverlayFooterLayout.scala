package org.skyluc.fan_resources.html.component

import org.skyluc.html.*

import Html.*

object HeaderMainOverlayFooterLayout {

  // tag names
  private val PAGE_HEADER = "header"
  private val PAGE_MAIN = "main"
  private val PAGE_FOOTER = "footer"
  // ids
  private val MAIN_CONTENT = "main-content"
  private val MAIN_OVERLAY = "main-overlay"
  private val MAIN_OVERLAY_CONTENT = "main-overlay-content"
  // classes
  private val CLASS_MAIN_OVERLAY_HIDDEN = "main-overlay-hidden"
  private val CLASS_MAIN_OVERLAY_CLOSE = "main-overlay-close"

  def generate(
      title: String,
      headerContent: Seq[BodyElement[?]],
      mainContent: Seq[BodyElement[?]],
      footerContent: Seq[BodyElement[?]],
  ): Seq[BodyElement[?]] = {
    Seq(
      div(PAGE_HEADER)
        .appendElements(headerContent*),
      div(PAGE_MAIN)
        .appendElements(
          div()
            .withId(MAIN_CONTENT)
            .appendElements(h1().appendElement(text(title)))
            .appendElements(mainContent*)
        ),
      div(PAGE_FOOTER)
        .appendElements(footerContent*),
      div()
        .withId(MAIN_OVERLAY)
        .withClass(CLASS_MAIN_OVERLAY_HIDDEN)
        .appendElements(
          div().withClass(CLASS_MAIN_OVERLAY_CLOSE).appendElements(text("⨯")).withOnClick("closeOverlay()"),
          div().withId(MAIN_OVERLAY_CONTENT),
        ),
    )
  }
}
