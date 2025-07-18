package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.yaml as fr

import fr.YamlParser.NodeWithRef
import fr.YamlReader.ParserError
import fr.Iding

object NodeToElement extends fr.NodeToElement {

  override def toElement(node: NodeWithRef, typeId: String): Either[ParserError, fr.Element] = {
    typeId match {
      case "site" => as[Site](node)
      case "page" => parsePage(node)
      case _ =>
        super.toElement(node, typeId)
    }
  }

  private def parsePage(node: NodeWithRef): Either[ParserError, fr.Element] = {
    for {
      iding <- as[Iding](node)
      res <- pageDispatch(node, iding.id)
    } yield {
      res
    }
  }

  private def pageDispatch(node: NodeWithRef, iding: String): Either[ParserError, fr.Element] = {
    iding match {
      case "music"      => as[MusicPage](node)
      case "shows"      => as[ShowsPage](node)
      case "chronology" => as[ChronologyPage](node)
      case u =>
        Left(ParserError(node.filename, error = Some(s"Unknown page id: '$u'")))
    }
  }
}
