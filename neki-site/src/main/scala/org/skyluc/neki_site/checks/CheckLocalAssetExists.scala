package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.checks as fr
import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.neki_site.data.{Site as dSite, *}

class CheckLocalAssetExists(imageBasePath: Path)
    extends fr.CheckLocalAssetExists(imageBasePath)
    with Processor[Seq[fr.CheckError]] {

  override def processSite(site: dSite): Seq[fr.CheckError] = Nil

}
