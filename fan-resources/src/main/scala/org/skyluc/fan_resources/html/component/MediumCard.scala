package org.skyluc.fan_resources.html.component

import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.html.*

import Html.*

object MediumCard {

  private val CLASS_MEDIUM_CARD_LIST = "medium-card-list"
  private val CLASS_MEDIUM_CARD_TREE = "medium-card-tree"
  private val CLASS_MEDIUM_CARD = "medium-card"
  private val CLASS_MEDIUM_CARD_DESIGNATION = "medium-card-designation"
  private val CLASS_MEDIUM_CARD_DATE = "medium-card-date"
  private val CLASS_MEDIUM_CARD_LABEL = "medium-card-label"
  private val CLASS_MEDIUM_CARD_SUBLABEL = "medium-card-sublabel"

  // ---------

  def generateList(list: Seq[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        list.map(generate(_))*
      )
  }

  def generate(compiledData: ElementCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(MediumImageCover.generate(compiledData.cover)),
      Some(div().withClass(CLASS_MEDIUM_CARD_DESIGNATION).appendElements(text(compiledData.designation))),
      Some(div().withClass(CLASS_MEDIUM_CARD_LABEL).appendElements(text(compiledData.label))),
      compiledData.sublabel.map(sublabel =>
        (div().withClass(CLASS_MEDIUM_CARD_SUBLABEL).appendElements(text(sublabel)))
      ),
      compiledData.displayDate.map(date => (div().withClass(CLASS_MEDIUM_CARD_DATE).appendElements(text(date)))),
    ).flatten

    a()
      .withClass(CLASS_MEDIUM_CARD)
      .withHref(compiledData.targetUrl.toString())
      .appendElements(
        elements*
      )
  }

  def generateTree(tree: LayeredData[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        tree.map(generateTree(_))*
      )
  }

  private def generateTree(node: LayeredNode[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_TREE)
      .appendElements(
        generate(node.data),
        SmallCard.generateTree(node.subLayer),
      )
  }

  def generateHybridTree(tree: LayeredData[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_LIST)
      .appendElements(
        tree.map(generateHybridTree(_))*
      )
  }

  private def generateHybridTree(node: LayeredNode[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_MEDIUM_CARD_TREE)
      .appendElements(
        if (node.subLayer.isEmpty) SmallCard.generate(node.data) else generate(node.data),
        SmallCard.generateTree(node.subLayer),
      )
  }

}
