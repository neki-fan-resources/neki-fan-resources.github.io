package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.BaseError

trait ProcessorWithError[A] extends fr.ProcessorWithError[A] {
  def processChronologyPage(chronologyPage: ChronologyPage): Either[BaseError, A]

  def processMusicPage(musicPage: MusicPage): Either[BaseError, A]

  def processSite(site: Site): Either[BaseError, A]

  def processShowsPage(showsPage: ShowsPage): Either[BaseError, A]

}
