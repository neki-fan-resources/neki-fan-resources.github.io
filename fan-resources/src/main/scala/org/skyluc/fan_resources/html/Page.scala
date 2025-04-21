package org.skyluc.fan_resources.html

// TODO: move Path outside of data package
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.component.HeaderMainOverlayFooterLayout
import org.skyluc.html.*

import Html.*

case class Url(text: String) {
  override def toString(): String = text
}

object Url {
  def apply(sections: String*): Url = Url(sections.mkString)
  def apply(path: Path): Url = Url(path.segments.mkString(SEPARATOR, SEPARATOR, Common.EMPTY))
  def apply(prefix: String, path: Path): Url = Url(prefix + path.segments.mkString(SEPARATOR))

  val SEPARATOR = "/"
}

trait Page {
  val outputPath: Path

  def content(): String
}

abstract class HtmlPage extends Page {
  val description: PageDescription

  override val outputPath: Path = description.outputPath

  def content(): String = {
    val renderer = new HtmlRenderer()
    renderer.visit(htmlContent())
    renderer.result
  }

  def htmlContent(): Html
}

trait PageDescription {
  val title: String
  val description: String
  val image: Url
  val canonicalUrl: Url
  val ogType: String
  val logo: Url
  val locale: String
  val outputPath: Path
}

abstract class SitePage extends HtmlPage {

  def headContent(): Seq[HeadElement[?]]

  def headerContent(): Seq[BodyElement[?]]

  def mainContent(): Seq[BodyElement[?]]

  def footerContent(): Seq[BodyElement[?]]

  override def htmlContent(): Html = {
    html()
      .withHead(
        head().appendElements(
          headContent()*
        )
      )
      .withBody(
        body().appendElements(
          HeaderMainOverlayFooterLayout.generate(
            description.title,
            headerContent(),
            mainContent(),
            footerContent(),
          )*
        )
      )
  }

}
