package org.skyluc.neki.html

import org.skyluc.html._
import Html._
import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode

object MediumCard {

  def generateList(list: List[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        list.map(generate(_))*
      )
  }

  def generate(data: ItemCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(img().withClass(CLASS_MEDIUM_CARD_COVER).withSrc(data.coverUrl).withAlt(data.coverAlt)),
      Some(div().withClass(CLASS_MEDIUM_CARD_DESIGNATION).appendElements(text(data.designation))),
      Some(div().withClass(CLASS_MEDIUM_CARD_LABEL).appendElements(text(data.label))),
      data.sublabel.map(sublabel => (div().withClass(CLASS_MEDIUM_CARD_SUBLABEL).appendElements(text(sublabel)))),
      data.displayDate.map(date => (div().withClass(CLASS_MEDIUM_CARD_DATE).appendElements(text(date)))),
    ).flatten

    a()
      .withClass(CLASS_MEDIUM_CARD)
      .withHref(data.url)
      .appendElements(
        elements*
      )
  }

  def generateTree(tree: LayeredData[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        tree.map(generateTree(_))*
      )
  }

  private def generateTree(node: LayeredNode[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_TREE)
      .appendElements(
        generate(node.data),
        SmallCard.generateTree(node.subLayer),
      )
  }

  def generateHybridTree(tree: LayeredData[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        tree.map(generateHybridTree(_))*
      )
  }

  private def generateHybridTree(node: LayeredNode[ItemCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_TREE)
      .appendElements(
        if (node.subLayer.isEmpty) SmallCard.generate(node.data) else generate(node.data),
        SmallCard.generateTree(node.subLayer),
      )
  }

  // --------------------

  val CLASS_MEDIUM_CARD_LIST = "medium-card-list"
  val CLASS_MEDIUM_CARD_TREE = "medium-card-tree"
  val CLASS_MEDIUM_CARD = "medium-card"
  val CLASS_MEDIUM_CARD_COVER = "medium-card-cover"
  val CLASS_MEDIUM_CARD_DESIGNATION = "medium-card-designation"
  val CLASS_MEDIUM_CARD_DATE = "medium-card-date"
  val CLASS_MEDIUM_CARD_LABEL = "medium-card-label"
  val CLASS_MEDIUM_CARD_SUBLABEL = "medium-card-sublabel"
}
