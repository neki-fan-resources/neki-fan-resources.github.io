package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.fan_resources.{checks => fr}
import org.skyluc.neki_site.data.*

object PopulateRelatedTo extends fr.PopulateRelatedTo with Processor[Datum[?]] {

  override def processChronologyPage(chronologyPage: ChronologyPage): Datum[?] = {
    chronologyPage
  }

  override def processMusicPage(musicPage: MusicPage): Datum[?] = {
    val references: Seq[Id[?]] = musicPage.music

    musicPage.withLinkedTo(references*)
  }

  override def processSite(site: Site): Datum[?] = {
    site
  }

  override def processShowsPage(showsPage: ShowsPage): Datum[?] = {
    val references: Seq[Id[?]] = showsPage.shows

    showsPage.withLinkedTo(references*)
  }

}
