package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.data.checks.DataCheck
import org.skyluc.neki_site.data.checks.LocalAssetExistsChecker
import org.skyluc.neki_site.data.checks.ReferencesChecker

import fr.Path
import org.skyluc.reference.html.edit.EditSupportContext

object Data {

  val dispatcherBuilder = new fr.op.DataDispatcherBuilder {

    override def build(dataBuilder: fr.op.DataProcessor): fr.op.DataDispatcher =
      op.DataDispatcher(dataBuilder)

  }

  val defaultExpanders = fr.op.DataLoader.defaultExpanders(op.ImplicitDataExpander)

  val defaultPopulaters =
    fr.op.DataLoader.defaultPopulaters(fr.op.MultimediaExtraPopulater(fr.op.MultimediaExtraProcessBuilder()))

  def defaultCheckers(staticFolderPath: Path) = DataCheck.defaultCheckers(
    ReferencesChecker,
    LocalAssetExistsChecker(staticFolderPath),
  )

  val editSupportContext = new EditSupportContext() {}

}
