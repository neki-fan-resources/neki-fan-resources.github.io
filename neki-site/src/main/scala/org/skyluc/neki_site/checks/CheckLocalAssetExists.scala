package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.checks as fr
import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.neki_site.Main
import org.skyluc.neki_site.data.{Site as dSite, *}
import org.skyluc.neki_site.html.Site

object CheckLocalAssetExists
    extends fr.CheckLocalAssetExists(Path(Main.STATIC_PATH).resolve(Site.BASE_IMAGE_ASSET_PATH))
    with Processor[Seq[fr.CheckError]] {

  override def processChronologyPage(chronologyPage: ChronologyPage): Seq[fr.CheckError] = Nil

  override def processMusicPage(musicPage: MusicPage): Seq[fr.CheckError] = Nil

  override def processSite(site: dSite): Seq[fr.CheckError] = Nil

  override def processShowsPage(showsPage: ShowsPage): Seq[fr.CheckError] = Nil

}
