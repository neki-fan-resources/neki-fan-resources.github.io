package org.skyluc.neki.html.page

import org.skyluc.html.Html

import java.nio.file.Path
import org.skyluc.neki.yaml.ParserError
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html.BodyElement


class ErrorPage(val errors: List[ParserError], data: Data) extends Page(data) {
  import ErrorPage._

  override def path(): Path = Path.of("404.html")

  override def shortTitle(): String = SHORT_TITLE

  override def altName(): Option[String] = None

  override def pageContent(): Html = {
    val errorText = errors.mkString
    Html.html().withBody(
      Html.body().appendElements(
        Html.h1().appendElement(Html.text("404")),
        Html.pre(errorText)
      )
    )
  }

  override def mainContent(): List[BodyElement[?]] = ???

}

object ErrorPage {
  val SHORT_TITLE = "Error page"
}

