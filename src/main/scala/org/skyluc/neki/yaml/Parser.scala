package org.skyluc.neki.yaml

import org.virtuslab.yaml.StringOps
import org.virtuslab.yaml.YamlError
import org.virtuslab.yaml.Node
import org.virtuslab.yaml.YamlCodec
import org.virtuslab.yaml.YamlDecoder
import org.virtuslab.yaml.LoadSettings

object Parser {

  def parse(yaml: String, fileName: String): Either[ParserError, Element] = {

    val node = yaml.asNode

    as[Typing](node, fileName)
      .map(_.`type`)
      .flatMap {
        case "album" => as[Album](node, fileName)
        case "song"  => as[Song](node, fileName)
        case u =>
          Left(ParserError(fileName, error = Some(s"Unknown type: '$u'")))
      }
  }

  private def as[T](node: Either[YamlError, Node], fileName: String)(implicit
      c: YamlDecoder[T]
  ): Either[ParserError, T] = {
    node
      .flatMap(_.as[T])
      .left
      .map(e => ParserError(fileName, yamlError = Some(e)))
  }
}

case class ParserError(
    id: String,
    error: Option[String] = None,
    yamlError: Option[YamlError] = None
) {
  override def toString(): String = {
    val ye = yamlError
      .map { e =>
        e.getMessage().replace("\n", "\n    ")
      }
      .getOrElse("")
    s"$id: ${error.getOrElse("")}${ye}"
  }
}

object ParserError {
  def apply(error: String): ParserError = ParserError("", Some(error))
  def apply(id: String, error: String): ParserError = ParserError(id, Some(error))
}
