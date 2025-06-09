package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

object Data {

  val creator = new fr.Data.DataBuilderProcessorCreator {
    def create(dataBuilder: fr.Data.DataBuilder): fr.Data.DataBuilderProcessor =
      DataBuilderProcessor(dataBuilder)

  }

  class DataBuilderProcessor(dataBuilder: fr.Data.DataBuilder)
      extends fr.Data.DataBuilderProcessor(dataBuilder)
      with Processor[Unit] {

    override def processSite(site: Site): Unit =
      dataBuilder.addDatum(site)

    override def processChronologyPage(chronologyPage: ChronologyPage): Unit =
      dataBuilder.addElement(chronologyPage)

    override def processMusicPage(musicPage: MusicPage): Unit =
      dataBuilder.addElement(musicPage)

    override def processShowsPage(showsPage: ShowsPage): Unit =
      dataBuilder.addElement(showsPage)
  }

}
