package org.skyluc.neki.html

import org.skyluc.html.Html

import java.nio.file.Path
import org.skyluc.neki.yaml.ParserError

case class ErrorPage(errors: List[ParserError]) extends Page {
  import ErrorPage._

  override def getPath(): Path = Path.of("404.html")

  override def getContent(): Html = {
    val errorText = errors.mkString
    Html.html().withBody(
      Html.body().appendElements(
        Html.h1().appendElement(Html.text("404")),
        Html.pre(errorText)
      )
    )
  }

  override def shortTitle(): String = SHORT_TITLE

  override def altName(): Option[String] = None

}

object ErrorPage {
  final val SHORT_TITLE = "Error page"
}

