package org.skyluc.neki

import java.nio.file.Paths
import org.skyluc.neki.yaml.{Parser, ParserResult}
import org.skyluc.neki.data.YamlFiles
import org.skyluc.neki.data.DataBuilder
import org.skyluc.neki.yaml.ToData
import org.skyluc.neki.site.Site

object Main {

  val DATA_PATH = "data"
  val OUTPUT_PATH = "target/site"

  def main(args: Array[String]): Unit = {

    val dataFolder = Paths.get(DATA_PATH)
    val outputFolder = Paths.get(OUTPUT_PATH)

    val dataFiles = YamlFiles.listAllFiles(dataFolder)

    val parserResults: List[ParserResult] = dataFiles.flatMap { path =>
      Parser.parse(path, path.subpath(dataFolder.getNameCount(), path.getNameCount()).toString())

    }

    val items = ToData.process(parserResults)

    println("--------------")

    println("PARSER ERRORS: ")
    items.flatMap(_.left.toOption).foreach { e =>
      println("  " + e)
    }
    println("--------------")

    val (dataErrors, data) =
      DataBuilder
        .load(items.flatMap(_.toOption))
        .crossReference()
        .checkReferences()
        .checkAssets()
        .expandRelatedTo()
        .checkReferenceLists()
        .done

    println("DATA ERRORS: ")
    dataErrors.foreach { e =>
      println("  " + e)
    }
    println("--------------")

    // println(data)

    Site.generate(data, items.flatMap(_.left.toOption) ::: dataErrors, outputFolder)

  }
}

trait SiteError {}
