package org.skyluc.neki_site

import org.skyluc.fan_resources.Main.displayErrors
import org.skyluc.fan_resources.checks.DataCheck
import org.skyluc.fan_resources.checks.MoreDataCheck
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.neki_site.checks.CheckLocalAssetExists
import org.skyluc.neki_site.checks.PopulateRelatedTo
import org.skyluc.neki_site.checks.ReferenceCheckProcessor
import org.skyluc.neki_site.data.Data
import org.skyluc.neki_site.data.ImplicitDatum
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data2Page.DataToPage
import org.skyluc.neki_site.html.CompiledDataGeneratorBuilder

object Main {

  def main(args: Array[String]): Unit = {
    main(Path())
  }

  def main(rootPath: Path): Unit = {

    println("\n***** Running NEK! Fan Resources site *****\n")

    val dataFolder = rootPath.resolve(DATA_PATH)
    val staticFolder = rootPath.resolve(STATIC_PATH)
    val frRootPath = rootPath.resolve(FAN_RESOURCES)
    val frStaticFolder = frRootPath.resolve(STATIC_PATH)
    val staticPiecesFolder = rootPath.resolve(STATIC_PIECES_PATH)
    val staticPiecesFrFolder = frRootPath.resolve(STATIC_PIECES_PATH)
    val outputFolder = rootPath.resolve(Path(TARGET_PATH, SITE_PATH))

    val (parserErrors, datums) = NekiSite.Parser001.parseFolder(dataFolder.asFilePath())

    displayErrors("TODATA ERRORS", parserErrors)

    val implicitDatums = ImplicitDatum().generate(datums)

    val d = fr.Data.get(datums ++ implicitDatums, Data.creator)

    val (checkErrors, checkedData) = DataCheck.check(
      datums ++ implicitDatums,
      d,
      PopulateRelatedTo,
      new ReferenceCheckProcessor(d.datums.keySet),
      new CheckLocalAssetExists(staticFolder.resolve(org.skyluc.neki_site.html.Site.BASE_IMAGE_ASSET_PATH)),
      false,
    )

    val data = fr.Data.get(checkedData, Data.creator)

    val moreCheckerrors = MoreDataCheck.check(data)

    displayErrors("CHECKS ERRORS", checkErrors ++ moreCheckerrors)

    val generator = CompiledDataGeneratorBuilder.generator(data)

    val site = generator.get(Site.ID)

    val pages =
      DataToPage(generator, staticPiecesFolder, staticPiecesFrFolder, site).generate(checkedData)

    SiteOutput.generate(pages, Seq(staticFolder.asFilePath(), frStaticFolder.asFilePath()), outputFolder.asFilePath())

  }

  // -----------

  val FAN_RESOURCES = "fan-resources"

  val DATA_PATH = "data"
  val STATIC_PATH = "static"
  val STATIC_PIECES_PATH = "static_pieces"
  val TARGET_PATH = "target"
  val SITE_PATH = "site"

}
