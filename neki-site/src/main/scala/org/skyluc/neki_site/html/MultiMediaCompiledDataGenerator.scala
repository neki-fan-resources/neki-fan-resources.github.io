package org.skyluc.neki_site.html

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html as fr
import org.skyluc.fan_resources.html.ImageWithOverlayCompiledData
import org.skyluc.fan_resources.html.MultiMediaCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.neki_site.data.{Site as dSite, *}

import fr.CompiledDataGenerator

class MultiMediaCompiledDataGenerator(generator: fr.CompiledDataGenerator) extends fr.MultiMediaCompiledDataGenerator {

  import MultiMediaCompiledDataGenerator.*

  val processor: MultiMediaCompiledDataGeneratorProcessor = new MultiMediaCompiledDataGeneratorProcessor(generator)

  val missingCompiledData: MultiMediaCompiledData = MultiMediaCompiledData(
    Common.SPACE,
    Common.MISSING,
    Date(2000, 1, 1),
    ImageWithOverlayCompiledData(
      MISSING_IMAGE_URL,
      OVERLAY_LOCAL_IMAGE_SOURCE,
      Common.MISSING,
      OVERLAY_LOCAL_IMAGE_ALT,
      true,
    ),
    None,
    Nil,
    false,
    MISSING_URL,
  )
}

class MultiMediaCompiledDataGeneratorProcessor(generator: CompiledDataGenerator)
    extends fr.MultiMediaCompiledDataGeneratorProcessor(generator)
    with Processor[MultiMediaCompiledData] {

  override def processChronologyPage(chronologyPage: ChronologyPage): MultiMediaCompiledData = ???

  override def processMusicPage(musicPage: MusicPage): MultiMediaCompiledData = ???

  override def processSite(site: dSite): MultiMediaCompiledData = ???

  override def processShowsPage(showsPage: ShowsPage): MultiMediaCompiledData = ???

}

object MultiMediaCompiledDataGenerator {

  val OVERLAY_PATH = Path("asset", "image", "overlay")
  val IMAGE_ALT = " media image"

  // local image

  val OVERLAY_LOCAL_IMAGE_SOURCE = Url(OVERLAY_PATH.resolve("empty.png"))
  val OVERLAY_LOCAL_IMAGE_ALT = "empty"

  val MISSING_URL = Url("/404")
  val MISSING_IMAGE_URL = Url(Site.BASE_IMAGE_ASSET_PATH.resolve(Path("site", "manekineko-200px.png")))

}
