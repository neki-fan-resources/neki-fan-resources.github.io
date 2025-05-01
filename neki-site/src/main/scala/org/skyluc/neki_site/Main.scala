package org.skyluc.neki_site

import org.skyluc.fan_resources.checks.DataCheck
import org.skyluc.fan_resources.element2data.DataTransformer
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.fan_resources.yaml.YamlReader
import org.skyluc.neki_site.checks.CheckLocalAssetExists
import org.skyluc.neki_site.checks.PopulateRelatedTo
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data2Page.DataToPage
import org.skyluc.neki_site.element2data.ElementToData
import org.skyluc.neki_site.html.CompiledDataGeneratorBuilder
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

    val (checkErrors, checkedData) = DataCheck.check(datums, PopulateRelatedTo, CheckLocalAssetExists)

    println("CHECKS ERRORS: ")
    checkErrors.foreach { e =>
      println("  " + e)
    }
    println("--------------")

    val generator = CompiledDataGeneratorBuilder.generator(checkedData)

    val site = generator.get(Site.ID)

    val pages =
      DataToPage(generator, site).generate(checkedData)

    println(s"nb of pages: ${pages.size}")

    SiteOutput.generate(pages, Seq(staticFolder), outputFolder)

  }

  // -----------

  val DATA_PATH = "data"
  val STATIC_PATH = "static"
  val TARGET_PATH = "target"
  val SITE_PATH = "site"

}
