package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.html.*

import Html.*

object MediumImageCover {

  // classes
  private val CLASS_MEDIUM_IMAGE_COVER = "medium-image-cover"

  def generate(compiledData: ImageCompiledData): BodyElement[?] = {
    compiledData.targetUrl
      .map { targetUrl =>
        a()
          .withClass(CLASS_MEDIUM_IMAGE_COVER)
          .withHref(targetUrl.toString)
          .appendElements(
            img()
              .withSrc(compiledData.source.toString)
              .withAlt(compiledData.alt)
          )
      }
      .getOrElse(
        img()
          .withClass(CLASS_MEDIUM_IMAGE_COVER)
          .withSrc(compiledData.source.toString())
          .withAlt(compiledData.alt)
      )
  }
}
