package org.skyluc.neki_site.html

import org.skyluc.fan_resources.html as fr

object LocalImageCopyCompiledDataGenerator extends fr.LocalImageCopyCompiledDataGenerator {

  val processor: fr.LocalImageCopyCompiledDataGeneratorProcessor = fr.LocalImageCopyCompiledDataGeneratorProcessor()

  override val missingCompiledData: fr.LocalImageCopyCompiledData = Site.MISSING_IMAGE
}
