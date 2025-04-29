package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.html as fr

object CompiledDataGeneratorBuilder {
  def generator(datums: Seq[Datum[?]]): fr.CompiledDataGenerator = {
    new fr.CompiledDataGenerator(
      datums,
      UrlResolver,
      elementGeneratorBuilder,
      multimediaGeneratorBuilder,
    )
  }

  def elementGeneratorBuilder(generator: fr.CompiledDataGenerator): fr.ElementCompiledDataGenerator = {
    new ElementCompiledDataGenerator(generator)
  }

  def multimediaGeneratorBuilder(generator: fr.CompiledDataGenerator): fr.MultiMediaCompiledDataGenerator = {
    new MultiMediaCompiledDataGenerator(generator)
  }
}
