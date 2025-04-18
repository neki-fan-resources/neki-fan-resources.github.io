package org.skyluc.html

case class HtmlTagAttributes(
    alt: Option[String],
    charset: Option[String],
    checked: Boolean,
    classes: List[String],
    content: Option[String],
    crossorigin: Boolean,
    dataDomain: Option[String],
    defer: Boolean,
    disabled: Boolean,
    height: Option[Int],
    href: Option[String],
    id: Option[String],
    lang: Option[String],
    onChange: Option[String],
    onClick: Option[String],
    name: Option[String],
    property: Option[String],
    rel: Option[String],
    sizes: Option[String],
    span: Option[Int],
    src: Option[String],
    translate: Boolean = true,
    target: Option[String],
    `type`: Option[String],
    value: Option[String],
    viewBox: Option[String],
    width: Option[Int],
) {
  def withAlt(alt: String): HtmlTagAttributes = copy(alt = Some(alt))
  def withCharset(charset: String): HtmlTagAttributes = copy(charset = Some(charset))
  def withChecked(checked: Boolean): HtmlTagAttributes = copy(checked = checked)
  def withClass(clazz: String): HtmlTagAttributes =
    copy(classes = clazz :: classes)
  def withContent(content: String): HtmlTagAttributes = copy(content = Some(content))
  def withCrossorigin(crossorigin: Boolean): HtmlTagAttributes = copy(crossorigin = crossorigin)
  def withDataDomain(dataDomain: String): HtmlTagAttributes = copy(dataDomain = Some(dataDomain))
  def withDefer(defer: Boolean): HtmlTagAttributes = copy(defer = defer)
  def withDisabled(disabled: Boolean): HtmlTagAttributes = copy(disabled = disabled)
  def withHeight(height: Int): HtmlTagAttributes = copy(height = Some(height))
  def withHref(href: String): HtmlTagAttributes = copy(href = Some(href))
  def withId(id: String): HtmlTagAttributes = copy(id = Some(id))
  def withLang(lang: String): HtmlTagAttributes = copy(lang = Some(lang))
  def withOnChange(script: String): HtmlTagAttributes =
    copy(onChange = Some(script))
  def withOnClick(script: String): HtmlTagAttributes =
    copy(onClick = Some(script))
  def withName(name: String): HtmlTagAttributes = copy(name = Some(name))
  def withProperty(property: String): HtmlTagAttributes = copy(property = Some(property))
  def withRel(rel: String): HtmlTagAttributes = copy(rel = Some(rel))
  def withSizes(sizes: String): HtmlTagAttributes = copy(sizes = Some(sizes))
  def withSpan(span: Int): HtmlTagAttributes = copy(span = Some(span))
  def withSrc(src: String): HtmlTagAttributes = copy(src = Some(src))
  def withTarget(target: String): HtmlTagAttributes = copy(target = Some(target))
  def withNoTranslate(): HtmlTagAttributes = copy(translate = false)
  def withType(`type`: String): HtmlTagAttributes = copy(`type` = Some(`type`))
  def withValue(value: String): HtmlTagAttributes = copy(value = Some(value))
  def withViewBox(viewBox: String): HtmlTagAttributes =
    copy(viewBox = Some(viewBox))
  def withWidth(width: Int): HtmlTagAttributes = copy(width = Some(width))
}

object HtmlTagAttributes {
  final val EMPTY =
    HtmlTagAttributes(
      None,
      None,
      false,
      Nil,
      None,
      false,
      None,
      false,
      false,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      true,
      None,
      None,
      None,
      None,
      None,
    )
}

trait HtmlVisited {
  def accept(v: Visitor): Unit
}

sealed trait HtmlTag[T <: HtmlTag[T]] extends HtmlVisited {
  val tag: String
  val attributes: HtmlTagAttributes
  def withClass(clazz: String): T
  def withId(id: String): T
  def withOnClick(script: String): T
  def withNoTranslate(): T
}

trait Html extends HtmlTag[Html] {
  val head: Option[Head]
  def withHead(head: Head): Html
  val body: Option[Body]
  def withBody(body: Body): Html
  def withLang(lang: String): Html
}

sealed trait HeadElement[T <: HeadElement[T]] extends HtmlTag[T]

trait Head extends HtmlTag[Head] {
  val elements: List[HeadElement[?]]
  def appendElements(e: HeadElement[?]*): Head
}

trait Link extends HeadElement[Link] {
  def withCrossorigin(crossorigin: Boolean): Link
  def withSizes(sizes: String): Link
  def withType(`type`: String): Link
}

trait Meta extends HeadElement[Meta] {
  def withCharset(charset: String): Meta
  def withContent(content: String): Meta
  def withName(name: String): Meta
  def withProperty(property: String): Meta
}

trait Title extends HeadElement[Title] {
  val text: String
}

sealed trait BodyElement[T <: BodyElement[T]] extends HtmlTag[T]

trait Body extends HtmlTag[Body] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): Body
}

trait Br extends BodyElement[Br] {}

trait A extends BodyElement[A] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): A
  def withHref(href: String): A
  def withName(name: String): A
  def withTarget(target: String): A
  def withTarget(target: Option[String]): A
}

object A {
  final val TARGET_BLANK = "_blank"
}

trait Canvas extends BodyElement[Canvas] {
  def withHeight(height: Int): Canvas
  def withWidth(width: Int): Canvas
}

trait Div extends BodyElement[Div] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): Div
}

trait H1 extends BodyElement[H1] {
  val elements: List[BodyElement[?]]
  def appendElement(e: BodyElement[?]): H1
}

trait H2 extends BodyElement[H2] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): H2
}

trait Img extends BodyElement[Img] {
  def withAlt(alt: String): Img
  def withSrc(src: String): Img
}

trait Input[T <: Input[T]] extends BodyElement[T] {
  val ype: String
}

trait InputButton extends Input[InputButton] {
  val ype = "button"
  val label: String
}

trait InputCheckbox extends Input[InputCheckbox] {
  val ype = "checkbox"
  def withChecked(checked: Boolean): InputCheckbox
  def withDisabled(disabled: Boolean): InputCheckbox
  def withOnChange(script: String): InputCheckbox
}

trait InputText extends Input[InputText] {
  val ype = "text"
}

trait InputTime extends Input[InputTime] {
  val ype = "time"
}

trait Li extends BodyElement[Li] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): Li
}

trait P extends BodyElement[P] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): P
}

trait Pre extends BodyElement[Pre] {
  val text: String
}

trait Script extends BodyElement[Script] with HeadElement[Script] {
  val script: Option[String]
  def setScript(s: String): Script
  def withDataDomain(dataDomain: String): Script
  def withDefer(defer: Boolean): Script
  def withSrc(src: String): Script
}

trait Span extends BodyElement[Span] {
  val elements: List[BodyElement[?]]
  def appendElement(e: BodyElement[?]): Span
}

trait Svg extends BodyElement[Svg] {
  val elements: List[SvgElement[?]]
  def appendElements(e: SvgElement[?]*): Svg
  def withHeight(height: Int): Svg
  def withViewBox(viewBox: String): Svg
  def withViewBox(left: Int, top: Int, width: Int, height: Int): Svg
  def withWidth(width: Int): Svg
}

trait Table extends BodyElement[Table] {
  val tbodys: List[Tbody]
  def appendTbody(tb: Tbody): Table
}

trait Tbody extends HtmlTag[Tbody] {
  val trs: List[Tr]
  def appendTrs(tr: Tr*): Tbody
}

trait Td extends HtmlTag[Td] {
  val elements: List[BodyElement[?]]
  def appendElements(e: BodyElement[?]*): Td
  def withSpan(span: Int): Td
}

trait Text extends BodyElement[Text] {
  val text: String
}

trait Tr extends HtmlTag[Tr] {
  val tds: List[Td]
  def appendTds(td: Td*): Tr
}

trait Ul extends BodyElement[Ul] {
  val elements: List[Li]
  def appendElements(e: Li*): Ul
}

object HtmlImpl {

  abstract class HtmlTagInt[T <: HtmlTag[T]](val tag: String) extends HtmlTag[T] {
    protected def copyWithAttributes(a: HtmlTagAttributes): T
    override def withClass(clazz: String): T = copyWithAttributes(
      attributes.withClass(clazz)
    )
    override def withId(id: String): T = copyWithAttributes(
      attributes.withId(id)
    )

    override def withOnClick(script: String): T = copyWithAttributes(
      attributes.withOnClick(script)
    )

    override def withNoTranslate(): T = copyWithAttributes(
      attributes.withNoTranslate()
    )
  }

  case class HtmlInt(
      attributes: HtmlTagAttributes,
      head: Option[Head],
      body: Option[Body],
  ) extends HtmlTagInt[Html]("html")
      with Html {

    override def withHead(head: Head): Html = copy(head = Some(head))

    override def withBody(body: Body): Html = copy(body = Some(body))

    override def withLang(lang: String): Html = copyWithAttributes(attributes.withLang(lang))

    override protected def copyWithAttributes(a: HtmlTagAttributes): HtmlInt =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class HeadInt(
      attributes: HtmlTagAttributes,
      elements: List[HeadElement[?]],
  ) extends HtmlTagInt[Head]("head")
      with Head {

    override protected def copyWithAttributes(a: HtmlTagAttributes): HeadInt =
      copy(attributes = a)

    override def appendElements(e: HeadElement[?]*): HeadInt =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class LinkInt(attributes: HtmlTagAttributes) extends HtmlTagInt[Link]("link") with Link {

    override protected def copyWithAttributes(a: HtmlTagAttributes): LinkInt =
      copy(attributes = a)

    override def withCrossorigin(crossorigin: Boolean): Link = copyWithAttributes(
      attributes.withCrossorigin(crossorigin)
    )

    override def withSizes(sizes: String): Link = copyWithAttributes(attributes.withSizes(sizes))

    override def withType(`type`: String): Link = copyWithAttributes(attributes.withType(`type`))

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class MetaInt(attributes: HtmlTagAttributes) extends HtmlTagInt[Meta]("meta") with Meta {

    override protected def copyWithAttributes(a: HtmlTagAttributes): MetaInt =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)

    override def withCharset(charset: String): Meta = copyWithAttributes(attributes.withCharset(charset))

    override def withContent(content: String): Meta = copyWithAttributes(attributes.withContent(content))

    override def withName(name: String): Meta = copyWithAttributes(attributes.withName(name))

    override def withProperty(property: String): Meta = copyWithAttributes(attributes.withProperty(property))
  }

  case class TitleInt(text: String) extends HtmlTagInt[Title]("title") with Title {
    override val attributes: HtmlTagAttributes = HtmlTagAttributes.EMPTY

    override protected def copyWithAttributes(a: HtmlTagAttributes): TitleInt = ???

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class BodyInt(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[Body]("body")
      with Body {

    override def appendElements(e: BodyElement[?]*): Body =
      copy(elements = elements ::: e.toList)

    override protected def copyWithAttributes(a: HtmlTagAttributes): BodyInt =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class AInt(attributes: HtmlTagAttributes, elements: List[BodyElement[?]]) extends HtmlTagInt[A]("a") with A {

    override protected def copyWithAttributes(a: HtmlTagAttributes): A = copy(attributes = a)

    override def appendElements(e: BodyElement[?]*): A = copy(elements = elements ::: e.toList)

    override def withHref(href: String): A = copyWithAttributes(attributes.withHref(href))

    override def withName(name: String): A = copyWithAttributes(attributes.withName(name))

    override def withTarget(target: String): A = copyWithAttributes(attributes.withTarget(target))

    override def withTarget(target: Option[String]): A =
      target.map(t => copyWithAttributes(attributes.withTarget(t))).getOrElse(this)

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class BrInt(attributes: HtmlTagAttributes) extends HtmlTagInt[Br]("br") with Br {
    override protected def copyWithAttributes(a: HtmlTagAttributes): Br = copy(attributes = a)
    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class CanvasInt(attributes: HtmlTagAttributes) extends HtmlTagInt[Canvas]("canvas") with Canvas {

    override def withHeight(height: Int): Canvas = copyWithAttributes(
      attributes.withHeight(height)
    )
    override def withWidth(width: Int): Canvas = copyWithAttributes(
      attributes.withWidth(width)
    )
    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Canvas =
      copy(attributes = a)
  }

  case class DivInt(
      attributes: HtmlTagAttributes,
      customTag: Option[String],
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[Div](customTag.getOrElse("div"))
      with Div {

    override def appendElements(e: BodyElement[?]*): Div =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Div =
      copy(attributes = a)
  }

  case class H1Int(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[H1]("h1")
      with H1 {

    override protected def copyWithAttributes(a: HtmlTagAttributes): H1Int =
      copy(attributes = a)

    override def appendElement(e: BodyElement[?]): H1Int =
      copy(elements = elements :+ e)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class H2Int(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[H2]("h2")
      with H2 {

    override protected def copyWithAttributes(a: HtmlTagAttributes): H2Int =
      copy(attributes = a)

    override def appendElements(e: BodyElement[?]*): H2Int =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class ImgInt(attributes: HtmlTagAttributes) extends HtmlTagInt[Img]("img") with Img {

    override protected def copyWithAttributes(a: HtmlTagAttributes): Img = copy(attributes = a)

    override def withAlt(alt: String): Img = copy(attributes = attributes.withAlt(alt))

    override def withSrc(src: String): Img = copy(attributes = attributes.withSrc(src))

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  abstract class InputInt[T <: Input[T]] extends HtmlTagInt[T]("input") with Input[T] {}

  case class InputButtonInt(label: String, attributes: HtmlTagAttributes)
      extends InputInt[InputButton]
      with InputButton {
    override protected def copyWithAttributes(
        a: HtmlTagAttributes
    ): InputButton = copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class InputCheckboxInt(attributes: HtmlTagAttributes) extends InputInt[InputCheckbox] with InputCheckbox {
    override protected def copyWithAttributes(
        a: HtmlTagAttributes
    ): InputCheckbox = copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)

    override def withChecked(checked: Boolean): InputCheckbox = copyWithAttributes(attributes.withChecked(checked))

    override def withDisabled(disabled: Boolean): InputCheckbox = copyWithAttributes(attributes.withDisabled(disabled))

    override def withOnChange(script: String): InputCheckbox = copyWithAttributes(attributes.withOnChange(script))
  }

  case class InputTextInt(attributes: HtmlTagAttributes) extends InputInt[InputText] with InputText {
    override protected def copyWithAttributes(a: HtmlTagAttributes): InputText =
      copy(attributes = a)
    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class InputTimeInt(attributes: HtmlTagAttributes) extends InputInt[InputTime] with InputTime {
    override protected def copyWithAttributes(a: HtmlTagAttributes): InputTime =
      copy(attributes = a)
    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class LiInt(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[Li]("li")
      with Li {

    override def appendElements(e: BodyElement[?]*): Li =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Li =
      copy(attributes = a)
  }

  case class PInt(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[P]("p")
      with P {

    override def appendElements(e: BodyElement[?]*): P =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): P =
      copy(attributes = a)
  }

  case class PreInt(attributes: HtmlTagAttributes, text: String) extends HtmlTagInt[Pre]("pre") with Pre {
    override protected def copyWithAttributes(a: HtmlTagAttributes): Pre =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class ScriptInt(attributes: HtmlTagAttributes, script: Option[String])
      extends HtmlTagInt[Script]("script")
      with HeadElement[Script]
      with Script {

    override protected def copyWithAttributes(a: HtmlTagAttributes): Script =
      copy(attributes = a)

    override def setScript(s: String): Script = copy(script = Some(s))

    override def withDataDomain(dataDomain: String): Script = copy(attributes = attributes.withDataDomain(dataDomain))

    override def withDefer(defer: Boolean): Script = copy(attributes = attributes.withDefer(defer))

    override def withSrc(src: String): Script =
      copy(attributes = attributes.withSrc(src))

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SpanInt(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[Span]("span")
      with Span {

    override protected def copyWithAttributes(a: HtmlTagAttributes): Span =
      copy(attributes = a)

    override def appendElement(e: BodyElement[?]): Span =
      copy(elements = elements :+ e)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgInt(
      attributes: HtmlTagAttributes,
      elements: List[SvgElement[?]],
  ) extends HtmlTagInt[Svg]("svg")
      with Svg {

    override protected def copyWithAttributes(a: HtmlTagAttributes): Svg =
      copy(attributes = a)

    override def appendElements(e: SvgElement[?]*): Svg =
      copy(elements = elements ::: e.toList)
    override def withHeight(height: Int): Svg =
      copy(attributes = attributes.withHeight(height))
    override def withViewBox(viewBox: String): Svg =
      copy(attributes = attributes.withViewBox(viewBox))

    override def withViewBox(left: Int, top: Int, width: Int, height: Int): Svg =
      withViewBox(s"$left $top $width $height")
    override def withWidth(width: Int): Svg =
      copy(attributes = attributes.withWidth(width))

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class TableInt(attributes: HtmlTagAttributes, tbodys: List[Tbody])
      extends HtmlTagInt[Table]("table")
      with Table {

    override def appendTbody(tb: Tbody): Table = copy(tbodys = tbodys :+ tb)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Table =
      copy(attributes = a)
  }

  case class TbodyInt(attributes: HtmlTagAttributes, trs: List[Tr]) extends HtmlTagInt[Tbody]("tbody") with Tbody {

    override def appendTrs(tr: Tr*): Tbody = copy(trs = trs ::: tr.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Tbody =
      copy(attributes = a)
  }

  case class TdInt(
      attributes: HtmlTagAttributes,
      elements: List[BodyElement[?]],
  ) extends HtmlTagInt[Td]("td")
      with Td {

    override def withSpan(span: Int): Td =
      copy(attributes = attributes.withSpan(span))

    override def appendElements(e: BodyElement[?]*): Td =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Td =
      copy(attributes = a)
  }

  case class TextInt(text: String) extends HtmlTag[Text] with Text {

    override def withClass(clazz: String): Text = ???

    override def withId(id: String): Text = ???

    override def withOnClick(script: String): Text = ???

    override def withNoTranslate(): Text = ???

    override val attributes: HtmlTagAttributes = HtmlTagAttributes.EMPTY

    override val tag = "-"

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class TrInt(attributes: HtmlTagAttributes, tds: List[Td]) extends HtmlTagInt[Tr]("tr") with Tr {

    override def appendTds(td: Td*): Tr = copy(tds = tds ::: td.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Tr =
      copy(attributes = a)
  }

  case class UlInt(
      attributes: HtmlTagAttributes,
      elements: List[Li],
  ) extends HtmlTagInt[Ul]("ul")
      with Ul {

    override def appendElements(e: Li*): Ul =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)

    override protected def copyWithAttributes(a: HtmlTagAttributes): Ul =
      copy(attributes = a)
  }

}

object Html {
  import HtmlImpl._

  def a(): A = AInt(HtmlTagAttributes.EMPTY, Nil)
  def body(): Body = BodyInt(HtmlTagAttributes.EMPTY, Nil)
  def br(): Br = BrInt(HtmlTagAttributes.EMPTY)
  def canvas(): Canvas = CanvasInt(HtmlTagAttributes.EMPTY)
  def div(): Div = DivInt(HtmlTagAttributes.EMPTY, None, Nil)
  def div(customTag: String): Div = DivInt(HtmlTagAttributes.EMPTY, Some(customTag), Nil)
  def head(): Head = HeadInt(HtmlTagAttributes.EMPTY, Nil)
  def html(): Html = HtmlInt(HtmlTagAttributes.EMPTY, None, None)
  def h1(): H1 = H1Int(HtmlTagAttributes.EMPTY, Nil)
  def h2(): H2 = H2Int(HtmlTagAttributes.EMPTY, Nil)
  def inputButton(label: String) =
    InputButtonInt(label, HtmlTagAttributes.EMPTY)
  def img(): Img = ImgInt(HtmlTagAttributes.EMPTY)
  def inputCheckbox(): InputCheckbox = InputCheckboxInt(HtmlTagAttributes.EMPTY)
  def inputText(): InputText = InputTextInt(HtmlTagAttributes.EMPTY)
  def inputTime(): InputTime = InputTimeInt(HtmlTagAttributes.EMPTY)
  def li(): Li = LiInt(HtmlTagAttributes.EMPTY, Nil)
  def link(rel: String, href: String): Link = LinkInt(
    HtmlTagAttributes.EMPTY.withRel(rel).withHref(href)
  )
  def meta(): Meta = MetaInt(HtmlTagAttributes.EMPTY)
  def p(): P = PInt(HtmlTagAttributes.EMPTY, Nil)
  def pre(text: String): Pre = PreInt(HtmlTagAttributes.EMPTY, text)
  def script(): Script = ScriptInt(HtmlTagAttributes.EMPTY, None)
  def span(): Span = SpanInt(HtmlTagAttributes.EMPTY, Nil)
  def svg(): Svg = SvgInt(HtmlTagAttributes.EMPTY, Nil)
  def table(): Table = TableInt(HtmlTagAttributes.EMPTY, Nil)
  def tbody(): Tbody = TbodyInt(HtmlTagAttributes.EMPTY, Nil)
  def td(): Td = TdInt(HtmlTagAttributes.EMPTY, Nil)
  def text(text: String): Text = TextInt(text)
  def title(text: String): Title = TitleInt(text)
  def tr(): Tr = TrInt(HtmlTagAttributes.EMPTY, Nil)
  def ul(): Ul = UlInt(HtmlTagAttributes.EMPTY, Nil)
}

object HtmlTag {}
