package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.html.*

import Html.*

object LargeDetails {

  private val CLASS_LARGE_DETAILS = "large-details"
  private val CLASS_LARGE_DETAILS_MAIN = "large-details-main"
  private val CLASS_LARGE_DETAILS_DESIGNATION = "large-details-designation"
  private val CLASS_LARGE_DETAILS_LABELS = "large-details-labels"
  private val CLASS_LARGE_DETAILS_LABEL = "large-details-label"
  private val CLASS_LARGE_DETAILS_SUBLABEL = "large-details-sublabel"
  private val CLASS_LARGE_DETAILS_INFO = "large-details-info"
  private val CLASS_LARGE_DETAILS_INFO_LABEL = "large-details-info-label"
  private val CLASS_LARGE_DETAILS_INFO_VALUE = "large-details-info-value"
  private val CLASS_LARGE_DETAILS_DESCRIPTION = "large-details-description"

  def generate(compiledData: ElementCompiledData): Div = {
    div()
      .withClass(CLASS_LARGE_DETAILS)
      .appendElements(
        LargeImageCover.generate(compiledData.cover),
        main(compiledData),
      )
  }

  private def main(compiledData: ElementCompiledData): Div = {

    val infoRows = compiledData.info.map { info =>
      val value: BodyElement[?] =
        info.url
          .map { url =>
            a().withHref(url.toString()).withTarget(Common.BLANK).appendElements(text(info.value))
          }
          .getOrElse(
            text(info.value)
          )
      tr().appendTds(
        td()
          .withClass(CLASS_LARGE_DETAILS_INFO_LABEL)
          .appendElements(
            text(info.label.getOrElse(Common.EMPTY))
          ),
        td()
          .withClass(CLASS_LARGE_DETAILS_INFO_VALUE)
          .appendElements(
            value
          ),
      )
    }

    val labels =
      div()
        .withClass(CLASS_LARGE_DETAILS_LABEL)
        .appendElements(
          text(compiledData.label)
        ) :: compiledData.sublabel
        .map { sublabel =>
          List(
            div()
              .withClass(CLASS_LARGE_DETAILS_SUBLABEL)
              .appendElements(
                text(sublabel)
              )
          )
        }
        .getOrElse(Nil)

    val elements: List[BodyElement[?]] = List(
      Some(
        div()
          .withClass(CLASS_LARGE_DETAILS_DESIGNATION)
          .appendElements(
            text(compiledData.designation)
          )
      ),
      Some(
        div()
          .withClass(CLASS_LARGE_DETAILS_LABELS)
          .appendElements(labels*)
      ),
      compiledData.parent.map(LineCard.generate(_)),
      Some(
        div()
          .withClass(CLASS_LARGE_DETAILS_INFO)
          .appendElements(
            table().appendTbody(
              tbody().appendTrs(
                infoRows*
              )
            )
          )
      ),
      compiledData.description.map { description =>
        div()
          .withClass(CLASS_LARGE_DETAILS_DESCRIPTION)
          .appendElements(
            description.map { line =>
              p().appendElements(text(line))
            }*
          )
      },
    ).flatten

    div()
      .withClass(CLASS_LARGE_DETAILS_MAIN)
      .appendElements(
        elements*
      )
  }

}
