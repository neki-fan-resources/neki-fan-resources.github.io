package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

trait Processor[T] extends fr.Processor[T] {
  def processChronologyPage(chronologyPage: ChronologyPage): T

  def processMusicPage(musicPage: MusicPage): T

  def processSite(site: Site): T

  def processShowsPage(showsPage: ShowsPage): T

}
