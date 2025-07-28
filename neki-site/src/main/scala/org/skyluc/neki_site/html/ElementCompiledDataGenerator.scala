package org.skyluc.neki_site.html

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html as fr
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.neki_site.data.{Site as dSite, *}

import fr.CompiledDataGenerator
import fr.LocalImageCopyCompiledData

class ElementCompiledDataGenerator(generator: fr.CompiledDataGenerator) extends fr.ElementCompiledDataGenerator {

  import ElementCompiledDataGenerator.*

  protected val processor: ElementCompiledDataGeneratorProcessor = new ElementCompiledDataGeneratorProcessor(generator)

  val missingCompiledData: fr.ElementCompiledData = fr.ElementCompiledData(
    Common.MISSING,
    Common.SPACE,
    Common.MISSING,
    None,
    None,
    None,
    None,
    Date(2000, 1, 1),
    None,
    MISSING_IMAGE,
    Nil,
    MISSING_URL,
    fr.DelayedElementCompiledDataSeq.EMPTY,
    fr.DelayedMultimediaCompiledData.EMPTY,
    Nil,
  )
}

class ElementCompiledDataGeneratorProcessor(generator: CompiledDataGenerator)
    extends fr.ElementCompiledDataGeneratorProcessor(generator)
    with Processor[ElementCompiledData] {

  override def processSite(site: dSite): ElementCompiledData = ???

}

object ElementCompiledDataGenerator {

  val MISSING_URL = Url("/404")
  val MISSING_IMAGE_URL = Url(Site.BASE_IMAGE_ASSET_PATH.resolve(Path("site", "manekineko-200px.png")))
  val MISSING_IMAGE = LocalImageCopyCompiledData(MISSING_IMAGE_URL, Common.MISSING)

}
