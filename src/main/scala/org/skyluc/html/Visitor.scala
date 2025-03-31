package org.skyluc.html

trait Visitor {
  def visit(a: A): Unit
  def visit(body: Body): Unit
  def visit(canvas: Canvas): Unit
  def visit(circle: SvgCircle): Unit
  def visit(desc: SvgDesc): Unit
  def visit(div: Div): Unit
  def visit(head: Head): Unit
  def visit(hmtl: Html): Unit
  def visit(h1: H1): Unit
  def visit(h2: H2): Unit
  def visit(img: Img): Unit
  def visit(input: Input[?]): Unit
  def visit(inputButton: InputButton): Unit
  def visit(line: SvgLine): Unit
  def visit(link: Link): Unit
  def visit(meta: Meta): Unit
  def visit(path: SvgPath): Unit
  def visit(pre: Pre): Unit
  def visit(script: Script): Unit
  def visit(span: Span): Unit
  def visit(svg: Svg): Unit
  def visit(table: Table): Unit
  def visit(tbody: Tbody): Unit
  def visit(td: Td): Unit
  def visit(text: Text): Unit
  def visit(text: SvgText): Unit
  def visit(title: Title): Unit
  def visit(tr: Tr): Unit
}
