package org.skyluc.html

case class SvgTagAttributes(
    classes: List[String],
    alignmentBaseline: Option[String],
    cx: Option[Double],
    cy: Option[Double],
    fill: Option[String],
    height: Option[Double],
    href: Option[String],
    id: Option[String],
    onClick: Option[String],
    preserveAspectRatio: Option[String],
    r: Option[Double],
    stroke: Option[String],
    textAnchor: Option[String],
    transform: Option[String],
    width: Option[Double],
    x: Option[Double],
    y: Option[Double],
    x1: Option[Double],
    x2: Option[Double],
    y1: Option[Double],
    y2: Option[Double],
) {
  def withClass(clazz: String): SvgTagAttributes =
    copy(classes = clazz :: classes)
  def withAlignmentBaseline(alignmentBaseline: String): SvgTagAttributes =
    copy(alignmentBaseline = Some(alignmentBaseline))
  def withCx(cx: Double): SvgTagAttributes = copy(cx = Some(cx))
  def withCy(cy: Double): SvgTagAttributes = copy(cy = Some(cy))
  def withId(id: String): SvgTagAttributes = copy(id = Some(id))
  def withFill(fill: String): SvgTagAttributes = copy(fill = Some(fill))
  def withHeight(height: Double): SvgTagAttributes = copy(height = Some(height))
  def withHref(href: String): SvgTagAttributes = copy(href = Some(href))
  def withOnClick(script: String): SvgTagAttributes =
    copy(onClick = Some(script))
  def withPreserveAspectRatio(par: String) = copy(preserveAspectRatio = Some(par))
  def withR(r: Double): SvgTagAttributes = copy(r = Some(r))
  def withStroke(stroke: String): SvgTagAttributes = copy(stroke = Some(stroke))
  def withTextAnchor(textAnchor: String): SvgTagAttributes =
    copy(textAnchor = Some(textAnchor))
  def withTransform(transform: String): SvgTagAttributes =
    copy(transform = Some(transform))
  def withWidth(width: Double): SvgTagAttributes = copy(width = Some(width))
  def withX(x: Double): SvgTagAttributes = copy(x = Some(x))
  def withY(y: Double): SvgTagAttributes = copy(y = Some(y))
  def withX1(x1: Double): SvgTagAttributes = copy(x1 = Some(x1))
  def withX2(x2: Double): SvgTagAttributes = copy(x2 = Some(x2))
  def withY1(y1: Double): SvgTagAttributes = copy(y1 = Some(y1))
  def withY2(y2: Double): SvgTagAttributes = copy(y2 = Some(y2))
}

object SvgTagAttributes {
  final val EMPTY = SvgTagAttributes(
    Nil,
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
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
  )
}

sealed trait SvgTag[T <: SvgTag[T]] extends HtmlVisited {
  val tag: String
  val attributes: SvgTagAttributes
  def withClass(clazz: String): T
  def withFill(fill: String): T
  def withId(id: String): T
  def withOnClick(script: String): T
  def withStroke(script: String): T
  def withTransform(script: String): T
}

sealed trait SvgElement[T <: SvgElement[T]] extends SvgTag[T]

trait SvgCircle extends SvgElement[SvgCircle] {
  def withCx(cx: Double): SvgCircle
  def withCy(cy: Double): SvgCircle
  def withR(r: Double): SvgCircle
}

trait SvgDesc extends SvgElement[SvgDesc] {
  val text: String
}

trait SvgG extends SvgElement[SvgG] {
  val elements: List[SvgElement[?]]
  def appendElements(e: SvgElement[?]*): SvgG
}

trait SvgImage extends SvgElement[SvgImage] {
  def withX(x: Double): SvgImage
  def withY(y: Double): SvgImage
  def withWidth(width: Double): SvgImage
  def withHeight(height: Double): SvgImage
  def withHref(href: String): SvgImage
  def withPreserveAspectRatio(par: String): SvgImage
}

trait SvgLine extends SvgElement[SvgLine] {
  def withX1(x1: Double): SvgLine
  def withX2(x2: Double): SvgLine
  def withY1(y1: Double): SvgLine
  def withY2(y2: Double): SvgLine
}

trait SvgPath extends SvgElement[SvgPath] {
  val d: String
}

trait SvgRect extends SvgElement[SvgRect] {
  def withX(x: Double): SvgRect
  def withY(y: Double): SvgRect
  def withWidth(width: Double): SvgRect
  def withHeight(height: Double): SvgRect
}

trait SvgStyle extends SvgElement[SvgStyle] {
  val style: String
}

trait SvgText extends SvgElement[SvgText] {
  val text: String

  def withX(x: Double): SvgText
  def withY(y: Double): SvgText
  def withTextAnchor(textAnchor: String): SvgText
  def withAlignmentBaseline(alignmentBaseline: String): SvgText
}

object SvgElementImpl {

  abstract class SvgTagInt[T <: SvgTag[T]](val tag: String) extends SvgTag[T] {
    protected def copyWithAttributes(a: SvgTagAttributes): T
    override def withClass(clazz: String): T = copyWithAttributes(
      attributes.withClass(clazz)
    )
    override def withFill(fill: String): T = copyWithAttributes(
      attributes.withFill(fill)
    )
    override def withId(id: String): T = copyWithAttributes(
      attributes.withId(id)
    )
    override def withOnClick(script: String): T = copyWithAttributes(
      attributes.withOnClick(script)
    )
    override def withStroke(stroke: String): T = copyWithAttributes(
      attributes.withStroke(stroke)
    )
    override def withTransform(transform: String): T = copyWithAttributes(
      attributes.withTransform(transform)
    )
  }

  case class SvgCircleImpl(attributes: SvgTagAttributes) extends SvgTagInt[SvgCircle]("circle") with SvgCircle {
    override def withCx(cx: Double): SvgCircle = copyWithAttributes(
      attributes.withCx(cx)
    )
    override def withCy(cy: Double): SvgCircle = copyWithAttributes(
      attributes.withCy(cy)
    )
    override def withR(r: Double): SvgCircle = copyWithAttributes(
      attributes.withR(r)
    )

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgCircle =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgDescImpl(text: String, attributes: SvgTagAttributes) extends SvgTagInt[SvgDesc]("desc") with SvgDesc {
    override protected def copyWithAttributes(a: SvgTagAttributes): SvgDesc =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgGInt(
      attributes: SvgTagAttributes,
      elements: List[SvgElement[?]],
  ) extends SvgTagInt[SvgG]("g")
      with SvgG {

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgG =
      copy(attributes = a)

    override def appendElements(e: SvgElement[?]*): SvgG =
      copy(elements = elements ::: e.toList)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgImageInt(attributes: SvgTagAttributes) extends SvgTagInt[SvgImage]("image") with SvgImage {

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgImage = copy(attributes = a)

    override def withX(x: Double): SvgImage = copyWithAttributes(attributes.withX(x))

    override def withY(y: Double): SvgImage = copyWithAttributes(attributes.withY(y))

    override def withWidth(width: Double): SvgImage = copyWithAttributes(attributes.withWidth(width))

    override def withHeight(height: Double): SvgImage = copyWithAttributes(attributes.withHeight(height))

    override def withHref(href: String): SvgImage = copyWithAttributes(attributes.withHref(href))

    override def withPreserveAspectRatio(par: String): SvgImage = copyWithAttributes(
      attributes.withPreserveAspectRatio(par)
    )

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgLineImpl(attributes: SvgTagAttributes) extends SvgTagInt[SvgLine]("line") with SvgLine {

    override def withX1(x1: Double): SvgLine = copyWithAttributes(
      attributes.withX1(x1)
    )
    override def withX2(x2: Double): SvgLine = copyWithAttributes(
      attributes.withX2(x2)
    )
    override def withY1(y1: Double): SvgLine = copyWithAttributes(
      attributes.withY1(y1)
    )
    override def withY2(y2: Double): SvgLine = copyWithAttributes(
      attributes.withY2(y2)
    )

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgLine =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgRectImpl(attributes: SvgTagAttributes) extends SvgTagInt[SvgRect]("rect") with SvgRect {

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgRect = copy(attributes = a)

    override def withX(x: Double): SvgRect = copyWithAttributes(attributes.withX(x))

    override def withY(y: Double): SvgRect = copyWithAttributes(attributes.withY(y))

    override def withWidth(width: Double): SvgRect = copyWithAttributes(attributes.withWidth(width))

    override def withHeight(height: Double): SvgRect = copyWithAttributes(attributes.withHeight(height))

    override def accept(v: Visitor): Unit = v.visit(this)

  }

  case class SvgPathImpl(d: String, attributes: SvgTagAttributes) extends SvgTagInt[SvgPath]("path") with SvgPath {
    override protected def copyWithAttributes(a: SvgTagAttributes): SvgPath =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgStyleImpl(style: String, attributes: SvgTagAttributes)
      extends SvgTagInt[SvgStyle]("style")
      with SvgStyle {
    override protected def copyWithAttributes(a: SvgTagAttributes): SvgStyle =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }

  case class SvgTextImpl(text: String, attributes: SvgTagAttributes) extends SvgTagInt[SvgText]("text") with SvgText {

    override def withX(x: Double): SvgText = copyWithAttributes(
      attributes.withX(x)
    )
    override def withY(y: Double): SvgText = copyWithAttributes(
      attributes.withY(y)
    )
    override def withTextAnchor(textAnchor: String): SvgText =
      copyWithAttributes(attributes.withTextAnchor(textAnchor))
    override def withAlignmentBaseline(alignmentBaseline: String): SvgText =
      copyWithAttributes(attributes.withAlignmentBaseline(alignmentBaseline))

    override protected def copyWithAttributes(a: SvgTagAttributes): SvgText =
      copy(attributes = a)

    override def accept(v: Visitor): Unit = v.visit(this)
  }
}

object SvgElement {

  import SvgElementImpl._

  def circle(cx: Double, cy: Double, r: Double): SvgCircle =
    SvgCircleImpl(SvgTagAttributes.EMPTY).withCx(cx).withCy(cy).withR(r)

  def desc(text: String): SvgDesc = SvgDescImpl(text, SvgTagAttributes.EMPTY)

  def g(): SvgG = SvgGInt(SvgTagAttributes.EMPTY, Nil)

  def image(x: Double, y: Double, width: Double, height: Double, href: String): SvgImage =
    SvgImageInt(SvgTagAttributes.EMPTY)
      .withX(x)
      .withY(y)
      .withWidth(width)
      .withHeight(height)
      .withHref(href)

  def line(x1: Double, y1: Double, x2: Double, y2: Double): SvgLine =
    SvgLineImpl(SvgTagAttributes.EMPTY)
      .withX1(x1)
      .withY1(y1)
      .withX2(x2)
      .withY2(y2)

  def path(d: String) = SvgPathImpl(d, SvgTagAttributes.EMPTY)

  def rect(x: Double, y: Double, width: Double, height: Double): SvgRect =
    SvgRectImpl(SvgTagAttributes.EMPTY)
      .withX(x)
      .withY(y)
      .withWidth(width)
      .withHeight(height)

  def style(style: String) = SvgStyleImpl(style, SvgTagAttributes.EMPTY)

  def text(x: Double, y: Double, text: String): SvgText =
    SvgTextImpl(text, SvgTagAttributes.EMPTY).withX(x).withY(y)
}
