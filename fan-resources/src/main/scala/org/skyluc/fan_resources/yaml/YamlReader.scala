package org.skyluc.fan_resources.yaml

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.yaml.YamlParser.NodeTree
import org.skyluc.scala.EitherOps
import org.virtuslab.yaml.YamlError

import java.io.FileReader
import java.io.Reader
import java.nio.CharBuffer
import java.nio.file.Files
import java.nio.file.Path
import scala.annotation.tailrec

object YamlReader {

  case class ParserError(
      id: String,
      error: Option[String] = None,
      yamlError: Option[YamlError] = None,
  ) extends BaseError {
    override def toString(): String = {
      val ye = yamlError
        .map { e =>
          e.getMessage().replace("\n", "\n    ")
        }
        .getOrElse("")
      s"$id: ${error.getOrElse("")}${ye}"
    }
  }

  case class NodeToElementResultTree(
      main: Either[ParserError, Element],
      related: Seq[NodeToElementResultTree],
  )

  case class ElementTree(
      main: Option[Element],
      related: Seq[ElementTree],
  )

  def load(dataFolder: Path, nodeToElement: NodeToElement): (Seq[ParserError], Seq[ElementTree]) = {
    val allYamlFiles = listAllYamlFiles(dataFolder)

    val allNodeTreesRes =
      allYamlFiles
        .map { file =>
          val filename = file.subpath(dataFolder.getNameCount(), file.getNameCount()).toString()
          YamlParser.extractNodes(readFile(file), filename)
        }

    val (parserErrors, nodeTrees) = EitherOps.errorsAndValuesFlatten(allNodeTreesRes)

    val allElementTreesRes = nodeTrees.map(nodeTreeToElementTree(_, nodeToElement))

    extractErrors(allElementTreesRes)
  }

  private def nodeTreeToElementTree(nodeTree: NodeTree, nodeToElement: NodeToElement): NodeToElementResultTree = {
    NodeToElementResultTree(
      nodeToElement.toElement(nodeTree.main),
      nodeTree.related.map(nodeTreeToElementTree(_, nodeToElement)),
    )
  }

  private def extractErrors(resultTrees: Seq[NodeToElementResultTree]): (Seq[ParserError], Seq[ElementTree]) = {
    val extracted = resultTrees.map(extractErrors)
    (extracted.flatMap(_._1), extracted.map(_._2))
  }

  private def extractErrors(resultTree: NodeToElementResultTree): (Seq[ParserError], ElementTree) = {
    val (relatedErrors, related) = extractErrors(resultTree.related)
    (
      resultTree.main.left.toSeq ++ relatedErrors,
      ElementTree(resultTree.main.toOption, related),
    )
  }

  // list all .yml files from a folder

  private def listAllYamlFiles(dataFolder: Path): List[Path] = {
    listAllYamlFiles(dataFolder :: Nil, Nil)
  }

  private def listAllYamlFiles(paths: List[Path], acc: List[Path]): List[Path] = {
    import scala.jdk.CollectionConverters._
    paths match {
      case head :: tail =>
        val folders: List[Path] = Files.list(head).filter(a => Files.isDirectory(a)).toList().asScala.toList
        val files: List[Path] = Files
          .list(head)
          .filter(a => Files.isRegularFile(a) && a.getFileName().toString().endsWith(".yml"))
          .toList()
          .asScala
          .toList
        listAllYamlFiles(folders ::: tail, acc ::: files)
      case Nil => acc
    }
  }

  // fully read a file

  private def readFile(path: Path): String = {
    val reader = new FileReader(path.toFile())
    val buffer = CharBuffer.allocate(1024)
    val builder = new StringBuilder()

    val content = readFile(reader, buffer, builder)

    reader.close()

    content
  }

  @tailrec private def readFile(reader: Reader, buffer: CharBuffer, builder: StringBuilder): String = {
    val lengthRead = reader.read(buffer)

    if (lengthRead > 0) {
      builder.append(buffer.slice(0, lengthRead))
      readFile(reader, buffer.clear(), builder)
    } else {
      builder.toString()
    }
  }

}
