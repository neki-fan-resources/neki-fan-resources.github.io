package org.skyluc.neki_site.data.op

import org.skyluc.fan_resources.data as fr
import org.skyluc.neki_site.data.*

class DataDispatcher(dataProcessor: fr.op.DataProcessor)
    extends fr.op.DataDispatcher(dataProcessor)
    with Processor[Unit] {

  override def processSite(site: Site): Unit =
    dataProcessor.processDatum(site)
}
