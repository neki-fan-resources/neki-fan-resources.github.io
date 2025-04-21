package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.html.*
import org.skyluc.html.*

import Html.*

object MediumDetails {

  def generate(compiledData: ElementCompiledData, infoLevel: Int): Div = {
    generate(
      compiledData.designation,
      compiledData.label,
      compiledData.sublabel,
      compiledData.cover,
      if (infoLevel <= ElementInfo.INFO_LEVEL_NONE) None else compiledData.parent,
      compiledData.info,
      infoLevel,
      Some(compiledData.targetUrl),
    )
  }

  def generate(label: String, cover: ImageCompiledData): Div = {
    generate(Common.EMPTY, label, None, cover, None, Nil, ElementInfo.INFO_LEVEL_NONE, None)
  }

  private def generate(
      designation: String,
      label: String,
      sublabel: Option[String],
      cover: ImageCompiledData,
      parent: Option[ElementCompiledData],
      info: List[ElementInfo],
      infoLevel: Int,
      targetUrl: Option[Url],
  ): Div = {

    val infoRows = info.filter(_.infoLevel <= infoLevel).map { info =>
      // TODO: generalize with LargeDetails
      val value: BodyElement[?] =
        info.url
          .map { u =>
            a().withHref(u.toString()).withTarget(Common.BLANK).appendElements(text(info.value))
          }
          .getOrElse(
            text(info.value)
          )
      tr().appendTds(
        td()
          .withClass(CLASS_MEDIUM_DETAILS_INFO_LABEL)
          .appendElements(
            text(info.label.getOrElse(Common.EMPTY))
          ),
        td()
          .withClass(CLASS_MEDIUM_DETAILS_INFO_VALUE)
          .appendElements(
            value
          ),
      )
    }

    val labels = Seq(
      Some(div().withClass(CLASS_MEDIUM_DETAILS_LABEL).appendElements(text(label))),
      sublabel.map { sl =>
        div()
          .withClass(CLASS_MEDIUM_DETAILS_SUBLABEL)
          .appendElements(text(sl))
      },
    ).flatten

    val labelsBlock: BodyElement[?] = targetUrl
      .map { u =>
        a()
          .withHref(u.toString())
          .withClass(CLASS_MEDIUM_DETAILS_LABELS)
          .appendElements(labels*)
      }
      .getOrElse {
        div()
          .withClass(CLASS_MEDIUM_DETAILS_LABELS)
          .appendElements(labels*)
      }

    val elements: List[BodyElement[?]] = List(
      Some(
        MediumImageCover.generate(cover)
      ),
      Some(div().withClass(CLASS_MEDIUM_DETAILS_DESIGNATION).appendElements(text(designation))),
      Some(labelsBlock),
      parent.map(p => LineCard.generate(p)),
      Some(
        div()
          .withClass(CLASS_MEDIUM_DETAILS_INFO)
          .appendElements(
            table().appendTbody(
              tbody().appendTrs(
                infoRows*
              )
            )
          )
      ),
    ).flatten

    div()
      .withClass(CLASS_MEDIUM_DETAILS)
      .appendElements(
        elements*
      )
  }

  private val CLASS_MEDIUM_DETAILS = "medium-details"
  private val CLASS_MEDIUM_DETAILS_DESIGNATION = "medium-details-designation"
  private val CLASS_MEDIUM_DETAILS_LABELS = "medium-details-labels"
  private val CLASS_MEDIUM_DETAILS_LABEL = "medium-details-label"
  private val CLASS_MEDIUM_DETAILS_SUBLABEL = "medium-details-sublabel"
  private val CLASS_MEDIUM_DETAILS_INFO = "medium-details-info"
  private val CLASS_MEDIUM_DETAILS_INFO_LABEL = "medium-details-info-label"
  private val CLASS_MEDIUM_DETAILS_INFO_VALUE = "medium-details-info-value"

}
