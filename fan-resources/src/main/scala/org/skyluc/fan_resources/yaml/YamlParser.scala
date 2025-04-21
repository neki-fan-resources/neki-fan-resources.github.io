package org.skyluc.fan_resources.yaml

import org.skyluc.fan_resources.yaml.YamlReader.ParserError
import org.virtuslab.yaml.Node
import org.virtuslab.yaml.Node.MappingNode
import org.virtuslab.yaml.Node.ScalarNode
import org.virtuslab.yaml.Node.SequenceNode
import org.virtuslab.yaml.StringOps
import org.virtuslab.yaml.YamlError

object YamlParser {

  case class NodeWithRef(node: Node, filename: String)

  case class NodeTree(
      main: NodeWithRef,
      related: Seq[NodeTree],
  )

  // Extract the nodes containing the individual datums
  def extractNodes(yaml: String, filename: String): Either[ParserError, Seq[NodeTree]] = {
    val rootRes = yaml.asNode.left.map(e => ParserError(filename, yamlError = Some(e)))

    rootRes.flatMap(extractNodeOrNodes(_, filename)).map { nodes =>
      nodes.map(extractNodeAndRelatedNodes(_, filename))
    }

  }

  // A file can contain one node, or a list of nodes
  // extract both case as a clean list of nodes
  private def extractNodeOrNodes(root: Node, filename: String): Either[ParserError, Seq[Node]] = {
    root match {
      case m: MappingNode =>
        Right(List(m))
      case SequenceNode(nodes) =>
        Right(nodes._1)
      case _ =>
        Left(ParserError(filename, Some("Invalid yaml structure")))
    }
  }

  // create a tree with main node and nodes in the related section
  private def extractNodeAndRelatedNodes(node: Node, filename: String): NodeTree = {
    val relatedNodes = extractRelatedNodes(node).map(extractNodeAndRelatedNodes(_, filename))
    NodeTree(NodeWithRef(node, filename), relatedNodes)
  }

  // extract the nodes from a list under the 'related' key
  private def extractRelatedNodes(node: Node): Seq[Node] = {
    node match {
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

}
