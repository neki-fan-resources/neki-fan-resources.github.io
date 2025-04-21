package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.{yaml => fr}

trait Processor[E, A] extends fr.Processor[E, A] {

  def processChronologyPage(chronologyPage: ChronologyPage): Either[E, A]

  def processMusicPage(musicPage: MusicPage): Either[E, A]

  def processShowsPage(showsPage: ShowsPage): Either[E, A]

  def processSite(site: Site): Either[E, A]

}
