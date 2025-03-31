package org.skyluc.neki.html

import org.skyluc.collection.LayeredData
import org.skyluc.html._
import Html._
import org.skyluc.collection.LayeredNode

object SmallCard {

  def generate(data: ItemCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(img().withClass(CLASS_SMALL_CARD_COVER).withSrc(data.coverUrl).withAlt(data.coverAlt)),
      Some(div().withClass(CLASS_SMALL_CARD_DESIGNATION).appendElements(text(data.designation))),
      Some(div().withClass(CLASS_SMALL_CARD_LABEL).appendElements(text(data.label))),
      data.sublabel.map(sublabel => (div().withClass(CLASS_SMALL_CARD_SUBLABEL).appendElements(text(sublabel)))),
      data.date.map(date => (div().withClass(CLASS_SMALL_CARD_DATE).appendElements(text(date)))),
    ).flatten

    a()
      .withClass(CLASS_SMALL_CARD)
      .withHref(data.url)
      .appendElements(
        elements*
      )
  }

  def generateTree(tree: LayeredData[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_SMALL_CARD_LIST)
      .appendElements(
        tree.map(generateTree(_))*
      )
  }

  private def generateTree(node: LayeredNode[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_SMALL_CARD_TREE)
      .appendElements(
        generate(node.data)
        // LineCard.generateTree(node.subLayer), // TODO: go to line card if needed
      )
  }

  // -------------------

  val CLASS_SMALL_CARD_LIST = "small-card-list"
  val CLASS_SMALL_CARD_TREE = "small-card-tree"
  val CLASS_SMALL_CARD = "small-card"
  val CLASS_SMALL_CARD_COVER = "small-card-cover"
  val CLASS_SMALL_CARD_DESIGNATION = "small-card-designation"
  val CLASS_SMALL_CARD_DATE = "small-card-date"
  val CLASS_SMALL_CARD_LABEL = "small-card-label"
  val CLASS_SMALL_CARD_SUBLABEL = "small-card-sublabel"

}
