package org.skyluc.neki.yaml

import org.virtuslab.yaml.StringOps
import org.virtuslab.yaml.YamlError
import org.virtuslab.yaml.Node
import org.virtuslab.yaml.YamlCodec
import org.virtuslab.yaml.YamlDecoder
import org.virtuslab.yaml.LoadSettings
import org.skyluc.neki.data.Id
import org.skyluc.neki.SiteError

object Parser {

  def parse(yaml: String, filename: String): Either[ParserError, Element] = {

    val nodeRes = yaml.asNode.left.map(e => ParserError(filename, yamlError = Some(e)))

    for {
      node <- nodeRes
      typing <- as[Typing](node, filename)
      res <- typeDispatch(node, typing, filename)
    } yield {
      res
    }
  }

  def typeDispatch(node: Node, typing: Typing, filename: String): Either[ParserError, Element] = {
    typing.`type` match {
      case "album" => as[Album](node, filename)
      case "song"  => as[Song](node, filename)
      case "site"  => as[Site](node, filename)
      case "show"  => as[Show](node, filename)
      case "page"  => parsePage(node, filename)
      case "tour"  => as[Tour](node, filename)
      case u =>
        Left(ParserError(filename, error = Some(s"Unknown type: '$u'")))
    }
  }

  def parsePage(node: Node, filename: String): Either[ParserError, Element] = {
    for {
      iding <- as[Iding](node, filename)
      res <- pageDispatch(node, iding, filename)
    } yield {
      res
    }
  }

  def pageDispatch(node: Node, iding: Iding, filename: String): Either[ParserError, Element] = {
    iding.id match {
      case "music" => as[MusicPage](node, filename)
      case "shows" => as[ShowsPage](node, filename)
      case u =>
        Left(ParserError(filename, error = Some(s"Unknown page id: '$u'")))
    }
  }

  private def as[T](node: Node, filename: String)(implicit
      c: YamlDecoder[T]
  ): Either[ParserError, T] = {
    node
      .as[T]
      .left
      .map(e => ParserError(filename, yamlError = Some(e)))
  }
}

case class ParserError(
    id: String,
    error: Option[String] = None,
    yamlError: Option[YamlError] = None,
) extends SiteError {
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
  def apply(id: Id[?], error: String): ParserError = ParserError(id.uid, Some(error))
}
