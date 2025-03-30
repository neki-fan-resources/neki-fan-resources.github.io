package org.skyluc.neki.html

import org.skyluc.html.Html._
import org.skyluc.neki.data.Item
import org.skyluc.neki.data.Data
import org.skyluc.html._

object ItemDetails {

  private case class CompiledDetails(
      label: String,
      sublabel: Option[String],
  )

  private class ItemDetails(data: ItemCompiledData) {
    def generate(): Div = {
      div()
        .withClass(CLASS_ITEM_DETAILS)
        .appendElements(
          cover(),
          main(),
        )
    }

    private def cover(): Img = {
      img().withClass(CLASS_ITEM_DETAILS_COVER).withSrc(data.coverUrl).withAlt(data.coverAlt)
    }

    private def main(): Div = {

      val infoRows = data.info.map { info =>
        tr().appendTds(
          td()
            .withClass(CLASS_ITEM_DETAILS_INFO_LABEL)
            .appendElement(
              text(info.label.getOrElse(CommonBase.EMPTY))
            ),
          td()
            .withClass(CLASS_ITEM_DETAILS_INFO_VALUE)
            .appendElement(
              text(info.value)
            ),
        )
      }

      val labels =
        div()
          .withClass(CLASS_ITEM_DETAILS_LABEL)
          .appendElements(
            text(data.label)
          ) :: data.sublabel
          .map { sublabel =>
            List(
              div()
                .withClass(CLASS_ITEM_DETAILS_SUBLABEL)
                .appendElements(
                  text(sublabel)
                )
            )
          }
          .getOrElse(Nil)

      val elements = List(
        div()
          .withClass(CLASS_ITEM_DETAILS_DESIGNATION)
          .appendElements(
            text(data.designation)
          ),
        div()
          .withClass(CLASS_ITEM_DETAILS_LABELS)
          .appendElements(labels*),
        div()
          .withClass(CLASS_ITEM_DETAILS_INFO)
          .appendElements(
            table().appendTbody(
              tbody().appendTrs(
                infoRows*
              )
            )
          ),
      )

      div()
        .withClass(CLASS_ITEM_DETAILS_MAIN)
        .appendElements(
          elements*
        )
    }

  }

  def generate(data: ItemCompiledData): Div = {
    new ItemDetails(data).generate()
  }

  // -------------

  val CLASS_ITEM_DETAILS = "item-details"
  val CLASS_ITEM_DETAILS_COVER = "item-details-cover"
  val CLASS_ITEM_DETAILS_MAIN = "item-details-main"
  val CLASS_ITEM_DETAILS_DESIGNATION = "item-details-designation"
  val CLASS_ITEM_DETAILS_LABELS = "item-details-labels"
  val CLASS_ITEM_DETAILS_LABEL = "item-details-label"
  val CLASS_ITEM_DETAILS_SUBLABEL = "item-details-sublabel"
  val CLASS_ITEM_DETAILS_INFO = "item-details-info"
  val CLASS_ITEM_DETAILS_INFO_LABEL = "item-details-info-label"
  val CLASS_ITEM_DETAILS_INFO_VALUE = "item-details-info-value"
}
