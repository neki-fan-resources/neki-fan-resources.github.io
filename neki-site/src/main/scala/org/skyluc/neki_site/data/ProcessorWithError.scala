package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

trait ProcessorWithError[E, A] extends fr.ProcessorWithError[E, A] {
  def processChronologyPage(chronologyPage: ChronologyPage): Either[E, A]

  def processMusicPage(musicPage: MusicPage): Either[E, A]

  def processSite(site: Site): Either[E, A]

  def processShowsPage(showsPage: ShowsPage): Either[E, A]

}
