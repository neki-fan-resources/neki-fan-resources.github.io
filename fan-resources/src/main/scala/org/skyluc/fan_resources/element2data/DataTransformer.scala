package org.skyluc.fan_resources.element2data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.yaml.Processor
import org.skyluc.fan_resources.yaml.YamlReader.ElementTree

object DataTransformer {

  case class ElementToDataResultTree(
      main: Option[Either[ToDataError, d.Datum[?]]],
      related: Seq[ElementToDataResultTree],
  )

  case class ToDataError(id: d.Id[?], errorMessage: String) extends BaseError {
    override def toString(): String = s"$id: $errorMessage"
  }

  def toData(
      elementTrees: Seq[ElementTree],
      processor: Processor[ToDataError, ElementToData.Result],
  ): (Seq[ToDataError], Seq[d.Datum[?]]) = {
    val resultTrees = elementTrees.map(toData(_, processor))

    val (errors, datums) = extractErrors(resultTrees)

    (errors, datums)
  }

  private def toData(
      elementTree: ElementTree,
      processor: Processor[ToDataError, ElementToData.Result],
  ): ElementToDataResultTree = {
    val result = elementTree.main.map(_.process(processor))

    val main = result.map(a => a.map(b => b.main))

    val additional = result.flatMap(a => a.map(b => b.additional).toOption).getOrElse(Nil)

    val related =
      elementTree.related.map(toData(_, processor)) ++ additional.map(e => ElementToDataResultTree(Some(Right(e)), Nil))

    // link related to their main
    val augmentedRelated = main
      .flatMap(_.toOption)
      .map { m =>
        related.map { resultTree =>
          resultTree.main
            .flatMap(_.toOption)
            .map { datum =>
              resultTree.copy(
                main = Some(Right(datum.withLinkedTo(m.id)))
              )
            }
            .getOrElse(resultTree)
        }
      }
      .getOrElse(related)

    // link main to the related
    val augmentedMain = main.map { mainEither =>
      mainEither.map { m =>
        related.foldLeft(m) { (acc, rel) =>
          rel.main
            .flatMap(_.toOption)
            .map { r =>
              acc.withLinkedTo(r.id)
            }
            .getOrElse(acc)
        }
      }
    }

    ElementToDataResultTree(augmentedMain, augmentedRelated)
  }

  private def extractErrors(resultTrees: Seq[ElementToDataResultTree]): (Seq[ToDataError], Seq[d.Datum[?]]) = {
    val extracted = resultTrees.map(extractErrors)
    (extracted.flatMap(_._1), extracted.flatMap(_._2))
  }

  private def extractErrors(resultTree: ElementToDataResultTree): (Seq[ToDataError], Seq[d.Datum[?]]) = {
    val (relatedErrors, related) = extractErrors(resultTree.related)
    (
      resultTree.main.flatMap(_.left.toOption).toSeq ++ relatedErrors,
      resultTree.main.flatMap(_.toOption).toSeq ++ related,
    )
  }

}
