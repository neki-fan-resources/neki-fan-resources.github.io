package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.html.*

import Html.*

object LargeImageCover {

  // classes
  private val CLASS_LARGE_IMAGE_COVER = "large-image-cover"

  def generate(compiledData: ImageCompiledData): BodyElement[?] = {
    compiledData.targetUrl
      .map { targetUrl =>
        a()
          .withClass(CLASS_LARGE_IMAGE_COVER)
          .withHref(targetUrl.toString)
          .appendElements(
            img()
              .withSrc(compiledData.source.toString)
              .withAlt(compiledData.alt)
          )
      }
      .getOrElse(
        img()
          .withClass(CLASS_LARGE_IMAGE_COVER)
          .withSrc(compiledData.source.toString())
          .withAlt(compiledData.alt)
      )
  }
}
