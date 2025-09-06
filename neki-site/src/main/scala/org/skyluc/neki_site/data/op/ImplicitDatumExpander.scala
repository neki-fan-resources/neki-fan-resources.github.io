package org.skyluc.neki_site.data.op

import org.skyluc.fan_resources.data as fr
import org.skyluc.neki_site.data.*

class ImplicitDatumExpander extends fr.op.ImplicitDatumExpander with Processor[Seq[fr.Datum[?]]] {

  override def processSite(site: Site): Seq[fr.Datum[?]] = Nil

}
