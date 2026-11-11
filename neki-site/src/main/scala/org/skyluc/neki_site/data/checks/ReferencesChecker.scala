package org.skyluc.neki_site.data.checks

import org.skyluc.fan_resources.data.DataView
import org.skyluc.fan_resources.data.checks as fr
import org.skyluc.neki_site.data.Processor
import org.skyluc.neki_site.data.Site

import fr.CheckError

object ReferencesChecker extends fr.ReferencesChecker(ReferencesCheckProcessorBuilder)

object ReferencesCheckProcessorBuilder extends fr.ReferencesCheckProcessorBuilder {
  override def build(data: DataView): fr.ReferencesCheckProcessor = ReferencesCheckProcessor(data)
}

class ReferencesCheckProcessor(data: DataView)
    extends fr.ReferencesCheckProcessor(data)
    with Processor[Seq[CheckError]] {

  override def processSite(site: Site): Seq[CheckError] =
    checkIds(site.id, fromDatum(site))
}
