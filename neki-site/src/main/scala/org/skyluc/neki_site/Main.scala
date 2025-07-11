package org.skyluc.neki_site

import org.skyluc.fan_resources.checks.DataCheck
import org.skyluc.fan_resources.checks.MoreDataCheck
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.element2data.DataTransformer
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.fan_resources.yaml.YamlReader
import org.skyluc.neki_site.checks.CheckLocalAssetExists
import org.skyluc.neki_site.checks.PopulateRelatedTo
import org.skyluc.neki_site.data.Data
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data2Page.DataToPage
import org.skyluc.neki_site.element2data.ElementToData
import org.skyluc.neki_site.html.CompiledDataGeneratorBuilder
import org.skyluc.neki_site.yaml.NodeToElement

object Main {

  def main(args: Array[String]): Unit = {
    main(Path())
  }

  def main(rootPath: Path): Unit = {

    println("\n***** Running NEK! Fan Resources site *****\n")

    val dataFolder = rootPath.resolve(DATA_PATH)
    val staticFolder = rootPath.resolve(STATIC_PATH)
    val staticPiecesFolder = rootPath.resolve(STATIC_PIECES_PATH)
    val outputFolder = rootPath.resolve(Path(TARGET_PATH, SITE_PATH))

    val (parserErrors, elements) = YamlReader.load(dataFolder.asFilePath(), new NodeToElement())

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

    val (checkErrors, checkedData) = DataCheck.check(
      datums,
      PopulateRelatedTo,
      new CheckLocalAssetExists(staticFolder.resolve(org.skyluc.neki_site.html.Site.BASE_IMAGE_ASSET_PATH)),
      false,
    )

    val data = fr.Data.get(checkedData, Data.creator)

    val moreCheckerrors = MoreDataCheck.check(data)

    println("CHECKS ERRORS: ")
    (checkErrors ++ moreCheckerrors).foreach { e =>
      println("  " + e)
    }
    println("--------------")

    val generator = CompiledDataGeneratorBuilder.generator(data)

    val site = generator.get(Site.ID)

    val pages =
      DataToPage(generator, staticPiecesFolder, site).generate(checkedData)

    println(s"nb of pages: ${pages.size}")

    SiteOutput.generate(pages, Seq(staticFolder.asFilePath()), outputFolder.asFilePath())

  }

  // -----------

  val DATA_PATH = "data"
  val STATIC_PATH = "static"
  val STATIC_PIECES_PATH = "static_pieces"
  val TARGET_PATH = "target"
  val SITE_PATH = "site"

}
