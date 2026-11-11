package org.skyluc.neki_site.data

import org.skyluc.fan_resources.base.Path
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.checks.DataCheck
import org.skyluc.neki_site.data.checks.LocalAssetExistsChecker
import org.skyluc.neki_site.data.checks.LocalAssetExistsChecker2
import org.skyluc.neki_site.data.checks.ReferencesChecker

object Data {

  val dispatcherBuilder = new fr.op.DataDispatcherBuilder {

    override def build(dataBuilder: fr.op.DataProcessor): fr.op.DataDispatcher =
      org.skyluc.neki_site.data.op.DataDispatcher(dataBuilder)

  }

  def defaultCheckers(staticFolderPath: Path) = DataCheck.defaultCheckers(
    ReferencesChecker,
    LocalAssetExistsChecker(staticFolderPath),
  )

  def defaultCheckers(staticFolderPath: Path, staticFolderPath2: Path) = DataCheck.defaultCheckers(
    ReferencesChecker,
    LocalAssetExistsChecker2(staticFolderPath, staticFolderPath2),
  )

}
