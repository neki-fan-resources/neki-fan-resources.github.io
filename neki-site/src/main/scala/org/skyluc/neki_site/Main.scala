package org.skyluc.neki_site

import org.skyluc.fan_resources.ErrorsHolder
import org.skyluc.fan_resources.Main.displayErrors
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.data.checks.DataCheck
import org.skyluc.fan_resources.html.SiteOutput
import org.skyluc.neki_site.html.ElementToPage

import fr.op.DataLoader

object Main {

  def main(args: Array[String]): Unit = {
    main(Path())
  }

  def main(rootPath: Path): Unit = {

    println("\n***** Running NEK! Fan Resources site *****\n")

    val configuration = MainSiteConfiguration(rootPath)

    val errors = ErrorsHolder()

    val data = errors.record("DATA LOADING", DataLoader.load(configuration.mainDataFolder, configuration))

    errors.append("DATA CHECKING", DataCheck.check(data, configuration.mainDataCheckers))

    displayErrors(errors, 10)

    if (errors.hasCriticalErrors) {
      System.exit(2)
    }

    val pages = ElementToPage.generate(data, configuration)

    SiteOutput.generate(pages, configuration)

  }

  private val ASSET_PATH = "asset"
  private val IMAGE_PATH = "image"

  val BASE_IMAGE_ASSET_PATH = Path(ASSET_PATH, IMAGE_PATH) // TODO: move into FR
}
