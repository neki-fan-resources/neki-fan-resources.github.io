package org.skyluc.neki_site.data.checks

import org.skyluc.fan_resources.data.checks as fr
import org.skyluc.fan_resources.yaml.Backup

object CheckBackupExistsBuilder extends fr.CheckBackupExistsBuilder {

  override def build(backup: Backup): fr.CheckBackupExists = new fr.CheckBackupExists(backup)

}
