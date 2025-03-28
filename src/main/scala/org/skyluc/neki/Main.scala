package org.skyluc.neki

import java.nio.file.Paths
import org.skyluc.neki.yaml.Parser
import java.io.FileReader
import java.io.BufferedReader
import java.nio.CharBuffer
import org.skyluc.neki.data.Data
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

    val dataFiles = Data.listAllFiles(dataFolder)

    println(dataFiles)

    val buffer = CharBuffer.allocate(10240)

    val parserResults = dataFiles.map { path =>
      buffer.clear()

      new FileReader(path.toFile()).read(buffer)

      buffer.rewind()

      val yaml = buffer.toString()

      Parser.parse(
        yaml,
        path.subpath(dataFolder.getNameCount(), path.getNameCount()).toString()
      )
    }

    val items = ToData.process(parserResults)

    println("ERRORS: ")
    items.flatMap(_.left.toOption).foreach { e =>
      println(e)
    }
    println("E-----")
    
    val data =
      DataBuilder.load(items.flatMap(_.toOption)).done

    println(data)

    Site.generate(data, items.flatMap(_.left.toOption), outputFolder)

  }
}
