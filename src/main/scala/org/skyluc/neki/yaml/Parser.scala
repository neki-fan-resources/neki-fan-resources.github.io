package org.skyluc.neki.yaml

import org.virtuslab.yaml.StringOps
import org.virtuslab.yaml.YamlError
import org.virtuslab.yaml.Node
import org.virtuslab.yaml.YamlDecoder
import org.skyluc.neki.data.Id
import org.skyluc.neki.SiteError
import org.virtuslab.yaml.Node.MappingNode
import org.virtuslab.yaml.Node.ScalarNode
import org.virtuslab.yaml.Node.SequenceNode

case class ParserResult(
    main: Either[ParserError, Element],
    related: List[ParserResult],
)

object Parser {

  def parse(yaml: String, filename: String): Seq[ParserResult] = {

    val nodeRes = yaml.asNode.left.map(e => ParserError(filename, yamlError = Some(e)))

    nodeRes.flatMap(nodeOrNodes(_, filename)) match {
      case Left(error) =>
        List(ParserResult(Left(error), Nil))
      case Right(nodes) =>
        nodes.map(parse(_, filename))
    }
  }

  def parse(node: Node, filename: String): ParserResult = {
    val mainElement = for {
      typing <- as[Typing](node, filename)
      res <- typeDispatch(node, typing, filename)
    } yield {
      res
    }

    ParserResult(
      mainElement,
      relatedNodes(node).map(parse(_, filename)).toList,
    )
  }

  def nodeOrNodes(root: Node, filename: String): Either[ParserError, Seq[Node]] = {
    root match {
      case m: MappingNode =>
        Right(List(m))
      case SequenceNode(nodes) =>
        Right(nodes._1)
      case _ =>
        Left(ParserError(filename, Some("Invalid yaml structure")))
    }
  }

  def relatedNodes(root: Node): Seq[Node] = {
    root match {
      case MappingNode(mappings, tag) =>
        mappings
          .find { (k, _) =>
            k match {
              case ScalarNode(value, tag) =>
                value == "related"
              case _ =>
                false
            }
          }
          .map { (_, v) =>
            v match {
              case SequenceNode(nodes, tag) =>
                nodes
              case _ =>
                Nil
            }
          }
          .getOrElse(Nil)
      case _ =>
        Nil
    }
  }

  def typeDispatch(node: Node, typing: Typing, filename: String): Either[ParserError, Element] = {
    typing.`type` match {
      case "album"        => as[Album](node, filename)
      case "media"        => as[Media](node, filename)
      case "song"         => as[Song](node, filename)
      case "site"         => as[Site](node, filename)
      case "show"         => as[Show](node, filename)
      case "page"         => parsePage(node, filename)
      case "tour"         => as[Tour](node, filename)
      case "youtubeshort" => as[YouTubeShort](node, filename)
      case "youtubevideo" => as[YouTubeVideo](node, filename)
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
      case "music"      => as[MusicPage](node, filename)
      case "shows"      => as[ShowsPage](node, filename)
      case "chronology" => as[ChronologyPage](node, filename)
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
