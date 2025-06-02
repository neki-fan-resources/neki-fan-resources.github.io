package org.skyluc.neki_reference.checks

import org.skyluc.fan_resources.checks as fr
import org.skyluc.neki_site.data.Processor
import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.yaml.Backup
import org.skyluc.neki_site.data.ShowsPage
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data.MusicPage
import org.skyluc.neki_site.data.ChronologyPage

class CheckBackupExists(backup: Backup) extends fr.CheckBackupExists(backup) with Processor[Option[BaseError]] {

  override def processChronologyPage(chronologyPage: ChronologyPage): Option[BaseError] = None

  override def processMusicPage(musicPage: MusicPage): Option[BaseError] = None

  override def processSite(site: Site): Option[BaseError] = None

  override def processShowsPage(showsPage: ShowsPage): Option[BaseError] = None

}

object CheckBackupExists {
  def builder(backup: Backup): CheckBackupExists = new CheckBackupExists(backup)
}
