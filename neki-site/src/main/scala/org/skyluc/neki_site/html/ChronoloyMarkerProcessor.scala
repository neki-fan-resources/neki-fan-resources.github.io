package org.skyluc.neki_site.html

import org.skyluc.fan_resources.html as fr
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.MarkerCompiledData
import org.skyluc.neki_site.data.*

class ChronoloyMarkerProcessor(refDay: Int, generator: CompiledDataGenerator)
    extends fr.ChronologyMarkerProcessor(refDay, generator)
    with Processor[MarkerCompiledData] {

  override def processChronologyPage(chronologyPage: ChronologyPage): MarkerCompiledData = ???

  override def processMusicPage(musicPage: MusicPage): MarkerCompiledData = ???

  override def processSite(site: Site): MarkerCompiledData = ???

  override def processShowsPage(showsPage: ShowsPage): MarkerCompiledData = ???

}
