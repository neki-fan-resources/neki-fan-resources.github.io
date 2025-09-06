package org.skyluc.neki_site

import org.skyluc.fan_resources.ErrorsHolder
import org.skyluc.fan_resources.Main.displayErrors
import org.skyluc.fan_resources.checks.DataCheck
import org.skyluc.fan_resources.checks.MoreDataCheck
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.neki_site.checks.CheckLocalAssetExists
import org.skyluc.neki_site.checks.ReferenceCheckProcessor
import org.skyluc.neki_site.data.Data
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data.op.ImplicitDatumExpander
import org.skyluc.neki_site.data2Page.DataToPage
import org.skyluc.neki_site.html.CompiledDataGeneratorBuilder
import org.skyluc.neki_site.yaml.NekiSiteDecoders

import fr.op.DataLoader

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

    val errors = ErrorsHolder()

    val (parserErrors, d) = DataLoader.load(dataFolder, NekiSiteDecoders, Data.creator, ImplicitDatumExpander())

    errors.append("DATA LOADING ERRORS", parserErrors, true)

    val (checkErrors, checkedData) = DataCheck.check(
      d.datums.values.toSeq,
      d,
      new ReferenceCheckProcessor(d.datums.keySet, d),
      new CheckLocalAssetExists(staticFolder.resolve(org.skyluc.neki_site.html.Site.BASE_IMAGE_ASSET_PATH)),
    )

    val (toDataError, data) = fr.Data.get(checkedData, Data.creator)

    val moreCheckerrors = MoreDataCheck.check(data)

    errors.append("CHECKS ERRORS", checkErrors ++ toDataError ++ moreCheckerrors, true)

    displayErrors(errors, 10)

    if (errors.hasCriticalErrors) {
      System.exit(2)
    }

    val generator = CompiledDataGeneratorBuilder.generator(data)

    val site = generator.get(Site.ID)

    val pages =
      DataToPage(generator, staticPiecesFolder, staticPiecesFrFolder, site).generate(checkedData)

    SiteOutput.generate(pages, Seq(staticFolder, frStaticFolder), outputFolder)

  }

  // -----------

  val FAN_RESOURCES = "fan-resources"

  val DATA_PATH = "data"
  val STATIC_PATH = "static"
  val STATIC_PIECES_PATH = "static_pieces"
  val TARGET_PATH = "target"
  val SITE_PATH = "site"

}
