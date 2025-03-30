package org.skyluc.html

import scala.collection.mutable

class HtmlRenderer extends Visitor {

  val builder: StringBuilder = new StringBuilder()
  var indent = 0

  override def visit(a: A): Unit = {
    writeTagsMultiLine(a) { () =>
      a.elements.foreach(_.accept(this))
    }
  }

  override def visit(body: Body): Unit = {
    writeTagsMultiLine(body) { () =>
      body.elements.foreach(_.accept(this))
    }
  }

  override def visit(canvas: Canvas): Unit = {
    writeTagsMultiLine(canvas) { () =>
      writeIndent()
      append("Sorry, your browser does not support canvas.")
      writeNewLine()
    }
  }

  override def visit(circle: SvgCircle): Unit = {
    writeTagSingle(circle)
  }

  override def visit(desc: SvgDesc): Unit = {
    writeTagsMultiLine(desc) { () =>
      append(desc.text)
    }
  }

  override def visit(div: Div): Unit = {
    writeTagsMultiLine(div) { () =>
      div.elements.foreach(_.accept(this))
    }
  }

  override def visit(head: Head): Unit = {
    writeTagsMultiLine(head) { () =>
      head.elements.foreach(_.accept(this))
    }
  }

  override def visit(html: Html): Unit = {
    append("<!DOCTYPE html>")
    writeNewLine()
    writeTagsMultiLine(html) { () =>
      html.head.foreach(_.accept(this))
      html.body.foreach(_.accept(this))
    }
  }

  override def visit(h1: H1): Unit = {
    writeTagsOneLine(h1) { () =>
      h1.elements.foreach(_.accept(this))
    }
  }

  override def visit(img: Img): Unit = {
    writeTagSingle(img)
  }

  override def visit(input: Input[?]): Unit = {
    writeTagSingle(input, _.withType(input.ype))
  }

  override def visit(inputButton: InputButton): Unit = {
    writeTagSingle(
      inputButton,
      _.withType(inputButton.ype).withValue(inputButton.label),
    )
  }

  override def visit(line: SvgLine): Unit = {
    writeTagSingle(line)
  }

  override def visit(link: Link): Unit = {
    writeTagSingle(link)
  }

  override def visit(meta: Meta): Unit = {
    writeTagSingle(meta)
  }

  override def visit(pre: Pre): Unit = {
    writeTagsInline(pre) { () =>
      append(pre.text)
    }
  }

  override def visit(path: SvgPath): Unit = {
    // copied from writeTagSingle(), because of the managment of the 'd' attribute
    writeIndent()
    append('<')
    append(path.tag)
    writeTagAttribute("d", path.d)
    writeTagAttributes(path.attributes)
    append("/>")
    writeNewLine()

  }

  override def visit(script: Script): Unit = {
    script.script
      .map { s =>
        writeTagsMultiLine(script) { () =>
          append(s)
          writeNewLine()
        }
      }
      .getOrElse {
        writeTagsOneLine(script)(() => ())
      }
  }

  override def visit(span: Span): Unit = {
    writeTagsInline(span) { () =>
      span.elements.foreach(_.accept(this))
    }
  }

  override def visit(svg: Svg): Unit = {
    writeTagsMultiLine(svg) { () =>
      svg.elements.foreach(_.accept(this))
    }
  }

  override def visit(table: Table): Unit = {
    writeTagsMultiLine(table) { () =>
      table.tbodys.foreach(_.accept(this))
    }
  }

  override def visit(tbody: Tbody): Unit = {
    writeTagsMultiLine(tbody) { () =>
      tbody.trs.foreach(_.accept(this))
    }
  }

  override def visit(td: Td): Unit = {
    writeTagsOneLine(td) { () =>
      td.elements.foreach(_.accept(this))
    }
  }

  override def visit(text: Text): Unit = {
    append(text.text)
  }

  override def visit(text: SvgText): Unit = {
    writeTagsOneLine(text) { () =>
      append(text.text)
    }
  }

  override def visit(text: Title): Unit = {
    writeTagsOneLine(text) { () =>
      append(text.text)
    }
  }

  override def visit(tr: Tr): Unit = {
    writeTagsMultiLine(tr) { () =>
      tr.tds.foreach(_.accept(this))
    }
  }

  def result: String = {
    builder.result()
  }

  private def moreIndent(): Unit = {
    indent += 1
  }

  private def lessIndent(): Unit = {
    indent -= 1
  }

  private def append(c: Char): Unit = {
    builder.append(c)
  }

  private def append(s: String): Unit = {
    builder.append(s)
  }

  private def writeIndent(): Unit = {
    (0 until indent).foreach { _ =>
      append("  ")
    }
  }

  private def writeNewLine(): Unit = {
    append('\n')
  }

  private def writeTagsMultiLine(
      element: HtmlTag[?]
  )(inside: () => Unit): Unit = {
    writeOpenTag(element)
    moreIndent()
    inside()
    lessIndent()
    writeCloseTag(element)
  }

  private def writeTagsOneLine(
      element: HtmlTag[?]
  )(inside: () => Unit): Unit = {
    writeOpenTagOneline(element)
    inside()
    writeCloseTagOneLine(element)
  }

  private def writeTagSingle(
      element: HtmlTag[?],
      attributesModifier: (HtmlTagAttributes) => HtmlTagAttributes = (a) => a,
  ): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(attributesModifier(element.attributes))
    append("/>")
    writeNewLine()
  }

  private def writeTagsInline(element: HtmlTag[?])(inside: () => Unit): Unit = {
    writeOpenTagInline(element)
    inside()
    writeCloseTagInline(element)
  }

  private def writeTagAttribute(attribute: String, value: String): Unit = {
    append(' ')
    append(attribute)
    append("=\"")
    append(value)
    append('"')
  }

  private def writeTagAttribute(attribute: String): Unit = {
    append(' ')
    append(attribute)
  }

  private def writeTagAttributes(attributes: HtmlTagAttributes): Unit = {
    attributes.id.foreach(writeTagAttribute("id", _))
    attributes.name.foreach(writeTagAttribute("name", _))
    attributes.property.foreach(writeTagAttribute("property", _))
    if (attributes.classes.nonEmpty) {
      writeTagAttribute("class", attributes.classes.mkString(" "))
    }
    attributes.rel.foreach(writeTagAttribute("rel", _))
    attributes.href.foreach(writeTagAttribute("href", _))
    attributes.src.foreach(writeTagAttribute("src", _))
    attributes.`type`.foreach(writeTagAttribute("type", _))
    attributes.charset.foreach(writeTagAttribute("charset", _))
    attributes.content.foreach(writeTagAttribute("content", _))
    if (attributes.crossorigin) writeTagAttribute("crossorigin")
    attributes.span.foreach((span: Int) =>
      writeTagAttribute("colspan", span.toString),
    )
    attributes.width.foreach((width: Int) =>
      writeTagAttribute("width", width.toString),
    )
    attributes.height.foreach((height: Int) =>
      writeTagAttribute("height", height.toString),
    )
    if (attributes.defer) writeTagAttribute("defer")
    attributes.dataDomain.foreach(writeTagAttribute("data-domain", _))
    attributes.sizes.foreach(writeTagAttribute("sizes", _))
    attributes.value.foreach(writeTagAttribute("value", _))
    attributes.viewBox.foreach(writeTagAttribute("viewBox", _))
    attributes.onClick.foreach(writeTagAttribute("onClick", _))
  }

  private def writeOpenTag(element: HtmlTag[?]): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append('>')
    writeNewLine()
  }

  private def writeOpenTagOneline(element: HtmlTag[?]): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append('>')
  }

  private def writeOpenTagInline(element: HtmlTag[?]): Unit = {
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append('>')
  }

  private def writeCloseTag(element: HtmlTag[?]): Unit = {
    writeIndent()
    append("</")
    append(element.tag)
    append('>')
    writeNewLine()
  }

  private def writeCloseTagOneLine(element: HtmlTag[?]): Unit = {
    append("</")
    append(element.tag)
    append('>')
    writeNewLine()
  }

  private def writeCloseTagInline(element: HtmlTag[?]): Unit = {
    append("</")
    append(element.tag)
    append('>')
  }

  private def writeTagsMultiLine(
      element: SvgTag[?]
  )(inside: () => Unit): Unit = {
    writeOpenTag(element)
    moreIndent()
    inside()
    lessIndent()
    writeCloseTag(element)
  }

  private def writeTagSingle(element: SvgTag[?]): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append("/>")
    writeNewLine()
  }

  private def writeTagsOneLine(element: SvgTag[?])(inside: () => Unit): Unit = {
    writeOpenTagOneline(element)
    inside()
    writeCloseTagOneLine(element)
  }

  private def writeOpenTag(element: SvgTag[?]): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append('>')
    writeNewLine()
  }

  private def writeOpenTagOneline(element: SvgTag[?]): Unit = {
    writeIndent()
    append('<')
    append(element.tag)
    writeTagAttributes(element.attributes)
    append('>')
  }

  private def writeCloseTag(element: SvgTag[?]): Unit = {
    writeIndent()
    append("</")
    append(element.tag)
    append('>')
    writeNewLine()
  }

  private def writeCloseTagOneLine(element: SvgTag[?]): Unit = {
    append("</")
    append(element.tag)
    append('>')
    writeNewLine()
  }

  private def writeTagAttribute(attribute: String, value: Double): Unit = {
    append(' ')
    append(attribute)
    append("=\"")
    append(value.toString())
    append('"')
  }

  private def writeTagAttributes(attributes: SvgTagAttributes): Unit = {
    attributes.id.foreach(writeTagAttribute("id", _))
    if (attributes.classes.nonEmpty) {
      writeTagAttribute("class", attributes.classes.mkString(" "))
    }
    attributes.x.foreach(writeTagAttribute("x", _))
    attributes.y.foreach(writeTagAttribute("y", _))
    attributes.x1.foreach(writeTagAttribute("x1", _))
    attributes.y1.foreach(writeTagAttribute("y1", _))
    attributes.x2.foreach(writeTagAttribute("x2", _))
    attributes.y2.foreach(writeTagAttribute("y2", _))
    attributes.cx.foreach(writeTagAttribute("cx", _))
    attributes.cy.foreach(writeTagAttribute("cy", _))
    attributes.r.foreach(writeTagAttribute("r", _))
    attributes.fill.foreach(writeTagAttribute("fill", _))
    attributes.stroke.foreach(writeTagAttribute("stroke", _))
    attributes.transform.foreach(writeTagAttribute("transform", _))
    attributes.textAnchor.foreach(writeTagAttribute("text-anchor", _))
    attributes.alignmentBaseline.foreach(
      writeTagAttribute("alignment-baseline", _)
    )
    attributes.onClick.foreach(writeTagAttribute("onClick", _))
  }

}

object HtmlRenderer {}
