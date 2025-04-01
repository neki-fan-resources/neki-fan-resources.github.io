package org.skyluc.neki

import java.nio.file.Paths
import org.skyluc.neki.yaml.Parser
import java.io.FileReader
import java.io.BufferedReader
import java.nio.CharBuffer
import org.skyluc.neki.data.YamlFiles
import org.skyluc.neki.data.DataBuilder
import org.skyluc.neki.yaml.ToData
import org.skyluc.neki.html.Pages
import org.skyluc.neki.site.Site

object Main {

  val DATA_PATH = "data"
  val OUTPUT_PATH = "target/site"

  def main(args: Array[String]): Unit = {
    println("Test")

    val dataFolder = Paths.get(DATA_PATH)
    val outputFolder = Paths.get(OUTPUT_PATH)

    val dataFiles = YamlFiles.listAllFiles(dataFolder)

    val buffer = CharBuffer.allocate(10240)

    val parserResults = dataFiles.map { path =>
      buffer.clear()

      val lengthRead = new FileReader(path.toFile()).read(buffer)

      val yaml = if (lengthRead > 0) {
        buffer.slice(0, lengthRead).toString()
      } else {
        ""
      }

      Parser.parse(
        yaml,
        path.subpath(dataFolder.getNameCount(), path.getNameCount()).toString(),
      )
    }

    val items = ToData.process(parserResults)

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
