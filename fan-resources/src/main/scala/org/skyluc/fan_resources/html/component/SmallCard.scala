package org.skyluc.fan_resources.html.component

import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.html.*

import Html.*

object SmallCard {

  private val CLASS_SMALL_CARD_LIST = "small-card-list"
  private val CLASS_SMALL_CARD_TREE = "small-card-tree"
  private val CLASS_SMALL_CARD = "small-card"
  private val CLASS_SMALL_CARD_DESIGNATION = "small-card-designation"
  private val CLASS_SMALL_CARD_DATE = "small-card-date"
  private val CLASS_SMALL_CARD_LABEL = "small-card-label"
  private val CLASS_SMALL_CARD_SUBLABEL = "small-card-sublabel"

  // -------------------

  def generate(compiledData: ElementCompiledData): A = {
    val elements: List[BodyElement[?]] = List(
      Some(MediumSmallImageCover.generate(compiledData.cover)),
      Some(div().withClass(CLASS_SMALL_CARD_DESIGNATION).appendElements(text(compiledData.designation))),
      Some(div().withClass(CLASS_SMALL_CARD_LABEL).appendElements(text(compiledData.label))),
      compiledData.sublabel.map(sublabel =>
        (div().withClass(CLASS_SMALL_CARD_SUBLABEL).appendElements(text(sublabel)))
      ),
      compiledData.displayDate.map(date => (div().withClass(CLASS_SMALL_CARD_DATE).appendElements(text(date)))),
    ).flatten

    a()
      .withClass(CLASS_SMALL_CARD)
      .withHref(compiledData.targetUrl.toString())
      .appendElements(
        elements*
      )
  }

  def generateTree(tree: LayeredData[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_SMALL_CARD_LIST)
      .appendElements(
        tree.map(generateTree(_))*
      )
  }

  private def generateTree(node: LayeredNode[ElementCompiledData]): Div = {
    div()
      .withClass(CLASS_SMALL_CARD_TREE)
      .appendElements(
        generate(node.data)
        // LineCard.generateTree(node.subLayer), // TODO: go to line card if needed
      )
  }

}
