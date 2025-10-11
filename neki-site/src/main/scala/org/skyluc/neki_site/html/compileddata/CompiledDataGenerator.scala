package org.skyluc.neki_site.html.compileddata

import org.skyluc.fan_resources.html as fr

object CompiledDataGeneratorProcessorsBuilder extends fr.compileddata.CompiledDataGeneratorProcessorsBuilder {

  override def elementCompiledDataProcessor(
      generator: fr.compileddata.CompiledDataGenerator
  ): fr.compileddata.ElementCompiledDataProcessor = ElementCompiledDataProcessor(generator)

  override def multimediaCompiledDataProcessor(
      generator: fr.compileddata.CompiledDataGenerator
  ): fr.compileddata.MultimediaCompiledDataProcessor =
    MultimediaCompiledDataProcessor(generator)

}
