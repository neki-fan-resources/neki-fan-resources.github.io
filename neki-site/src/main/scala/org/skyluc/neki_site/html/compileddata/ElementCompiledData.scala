package org.skyluc.neki_site.html.compileddata

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Id
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.data.ProcessorElement

import fr.compileddata.ElementCompiledData

class ElementCompiledDataProcessor(generator: fr.compileddata.CompiledDataGenerator)
    extends fr.compileddata.ElementCompiledDataProcessor(generator)
    with ProcessorElement[fr.compileddata.ElementCompiledData] {

  override def missingElementCompiledData: ElementCompiledData =
    fr.compileddata.ElementCompiledData(
      Id.UNDEFINED,
      Common.MISSING,
      Common.MISSING,
      None,
      None,
      ElementCompiledData.FixedDelayedCompiledData(MultimediaCompiledDataProcessor.missingMultimediaCompiledData),
      Common.MISSING_DATE,
      Nil,
      None,
      ElementCompiledData.DelayedOptionCompiledDataEmpty(),
      ElementCompiledData.DelayedSeqCompiledDataEmpty(),
      ElementCompiledData.DelayedOptionCompiledDataEmpty(),
      Nil,
    )

}
