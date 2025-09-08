package org.skyluc.neki_site.data.op

import org.skyluc.fan_resources.data as fr
import org.skyluc.neki_site.data.*

class DataDispatcher(dataBuilder: fr.op.DataBuilder) extends fr.op.DataDispatcher(dataBuilder) with Processor[Unit] {

  override def processSite(site: Site): Unit =
    dataBuilder.addDatum(site)
}
