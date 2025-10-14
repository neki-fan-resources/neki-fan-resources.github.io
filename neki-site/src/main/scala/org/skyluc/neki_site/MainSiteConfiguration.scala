package org.skyluc.neki_site

import org.skyluc.fan_resources as fr
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.yaml.NekiSiteDecoders

import fr.data.Path

class MainSiteConfiguration(override val rootFolder: Path) extends fr.MainSiteConfiguration {

  import MainSiteConfiguration.*

  override val decoders: fr.yaml.FrDecoders = NekiSiteDecoders

  override val dispatcherBuilder: fr.data.op.DataDispatcherBuilder = DispatcherBuilder

  override def referenceChecker: fr.data.checks.ReferencesChecker = d.checks.ReferencesChecker

}

object MainSiteConfiguration {

  object DispatcherBuilder extends fr.data.op.DataDispatcherBuilder {

    override def build(dataBuilder: fr.data.op.DataProcessor): fr.data.op.DataDispatcher =
      d.op.DataDispatcher(dataBuilder)

  }

}
