package org.skyluc.fan_resources.html.component

import org.skyluc.html.*

import Html.*

object SectionHeader {

  def generate(title: String): H2 = {
    h2().appendElements(text(title))
  }

  def generateWithStatus(title: String, status: String, statusCode: String): H2 = {
    generate(
      text(title),
      span()
        .withClass(Status.CLASS_STATUS)
        .withClass(Status.CLASS_STATUS_BASE + statusCode)
        .appendElement(
          text(status)
        ),
    )
  }

  def generate(content: BodyElement[?]*): H2 = {
    h2().appendElements(content*)
  }
}

object Status {
  val CLASS_STATUS = "status"
  val CLASS_STATUS_BASE = "status-"
}
