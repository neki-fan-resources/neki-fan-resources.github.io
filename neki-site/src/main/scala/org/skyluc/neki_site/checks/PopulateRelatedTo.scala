package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.checks as fr
import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.neki_site.data.*

object PopulateRelatedTo extends fr.PopulateRelatedTo with Processor[Datum[?]] {

  override def processSite(site: Site): Datum[?] = {
    site
  }

}
