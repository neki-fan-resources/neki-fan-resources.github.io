package org.skyluc.neki_site

import org.skyluc.fan_resources as fr
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.data.ExtendedDatum

import fr.base.Path
import fr.descriptor.ChoiceCategoryRepository

class MainSiteConfiguration(override val rootFolder: Path) extends fr.MainSiteConfiguration {

  import MainSiteConfiguration.*

  override val categoryRepository: ChoiceCategoryRepository = ExtendedDatum.categoryRepository

  override val dispatcherBuilder: fr.data.op.DataDispatcherBuilder = DispatcherBuilder

  override def referenceChecker: fr.data.checks.ReferencesChecker = d.checks.ReferencesChecker

}

object MainSiteConfiguration {

  object DispatcherBuilder extends fr.data.op.DataDispatcherBuilder {

    override def build(dataBuilder: fr.data.op.DataProcessor): fr.data.op.DataDispatcher =
      d.op.DataDispatcher(dataBuilder)

  }

}
