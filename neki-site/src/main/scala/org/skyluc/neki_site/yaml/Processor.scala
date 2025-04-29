package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.{yaml => fr}
import org.skyluc.fan_resources.BaseError

trait Processor[A] extends fr.Processor[A] {

  def processChronologyPage(chronologyPage: ChronologyPage): Either[BaseError, A]

  def processMusicPage(musicPage: MusicPage): Either[BaseError, A]

  def processShowsPage(showsPage: ShowsPage): Either[BaseError, A]

  def processSite(site: Site): Either[BaseError, A]

}
