package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.checks as fr
import org.skyluc.fan_resources.data.Id
import org.skyluc.neki_site.data.Processor
import org.skyluc.neki_site.data.Site

import fr.CheckError

class ReferenceCheckProcessor(refIds: Set[Id[?]])
    extends fr.ReferencesCheckProcessor(refIds)
    with Processor[Seq[CheckError]] {

  override def processSite(site: Site): Seq[CheckError] =
    checkIds(site.id, fromDatum(site))
}
