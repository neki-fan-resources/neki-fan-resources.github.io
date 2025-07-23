package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.Data
import org.skyluc.fan_resources.data.Id
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.data.Processor

import fr.CompiledDataGenerator
import fr.DatumCompiledData

class DatumCompiledDataGenerator(data: Data, backupPath: Path, generator: CompiledDataGenerator)
    extends fr.DatumCompiledDataGenerator(data, backupPath, generator)
    with Processor[DatumCompiledData] {

  override def missingCompiledData(id: Id[?]): DatumCompiledData = DatumCompiledData(
    id,
    Site.MISSING_IMAGE,
  )

  override def processSite(site: org.skyluc.neki_site.data.Site): DatumCompiledData = {
    val attributes = Seq()

    DatumCompiledData(
      site.id,
      "Site",
      generator.missingElement.cover,
      attributes,
      site.linkedTo,
    )
  }

}
