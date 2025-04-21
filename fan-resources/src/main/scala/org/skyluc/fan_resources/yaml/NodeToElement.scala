package org.skyluc.fan_resources.yaml

import org.skyluc.fan_resources.yaml.YamlParser.NodeWithRef
import org.skyluc.fan_resources.yaml.YamlReader.ParserError
import org.virtuslab.yaml.YamlDecoder

class NodeToElement {

  // method to extend to support more types
  def toElement(node: NodeWithRef, typeId: String): Either[ParserError, Element] = {
    typeId match {
      case "album"        => as[Album](node)
      case "localImage"   => as[LocalImage](node)
      case "mediaaudio"   => as[MediaAudio](node)
      case "mediawritten" => as[MediaWritten](node)
      case "song"         => as[Song](node)
      case "show"         => as[Show](node)
      case "postx"        => as[PostX](node)
      case "tour"         => as[Tour](node)
      case "youtubeshort" => as[YouTubeShort](node)
      case "youtubevideo" => as[YouTubeVideo](node)
      case "zaiko"        => as[Zaiko](node)
      case u =>
        Left(ParserError(node.filename, error = Some(s"Unknown type in NodeToElement: '$u'")))
    }
  }

  final def toElement(node: NodeWithRef): Either[ParserError, Element] = {
    for {
      typing <- as[Typing](node)
      res <- toElement(node, typing.`type`)
    } yield {
      res
    }
  }

  protected def as[T](node: NodeWithRef)(implicit
      c: YamlDecoder[T]
  ): Either[ParserError, T] = {
    node.node
      .as[T]
      .left
      .map(e => ParserError(node.filename, yamlError = Some(e)))
  }
}
