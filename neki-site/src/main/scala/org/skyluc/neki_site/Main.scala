package org.skyluc.neki_site

import org.skyluc.fan_resources.element2data.DataTransformer
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.fan_resources.yaml.YamlReader
import org.skyluc.neki_site.data.Data
import org.skyluc.neki_site.data2Page.DataToPage
import org.skyluc.neki_site.element2data.ElementToData
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.yaml.NodeToElement

import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {
    val dataFolder = Paths.get(DATA_PATH)
    val staticFolder = Paths.get(STATIC_PATH)
    val outputFolder = Paths.get(TARGET_PATH, SITE_PATH)

    val (parserErrors, elements) = YamlReader.load(dataFolder, new NodeToElement())

    println("--------------")

    println("PARSER ERRORS: ")
    parserErrors.foreach { e =>
      println("  " + e)
    }
    println("--------------")

    val (toDataErrors, datums) =
      DataTransformer.toData(elements, ElementToData)

    println("TODATA ERRORS: ")
    toDataErrors.foreach { e =>
      println("  " + e)
    }
    println("--------------")

    val data = Data(datums)

    // TODO: all the checks

    val compilers = Compilers(data)

    val pages = DataToPage(compilers).generate(data.all.values.toSeq)

    println(s"nb of pages: ${pages.size}")

    SiteOutput.generate(pages, staticFolder, outputFolder)

  }

  // -----------

  val DATA_PATH = "data"
  val STATIC_PATH = "static"
  val TARGET_PATH = "target"
  val SITE_PATH = "site"

}
