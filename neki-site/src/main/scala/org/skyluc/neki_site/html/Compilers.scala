package org.skyluc.neki_site.html

import org.skyluc.neki_site.data.Data

case class Compilers(
    data: Data
) {
  var elementDataCompiler: ElementCompiledDataGenerator = null
  var multimediaDataCompiler: MultiMediaCompiledDataGenerator = null

  {
    elementDataCompiler = ElementCompiledDataGenerator(this)
    multimediaDataCompiler = MultiMediaCompiledDataGenerator(this)
  }
}
