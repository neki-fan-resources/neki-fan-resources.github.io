package org.skyluc.neki_site.data.op

import org.skyluc.fan_resources.data as fr
import org.skyluc.neki_site.data.*

object ImplicitDataExpander extends fr.op.ImplicitDataExpander {

  override val expanderProcessor: ImplicitDatumExpanderProcessor = ImplicitDatumExpanderProcessor()

}

class ImplicitDatumExpanderProcessor extends fr.op.ImplicitDatumExpanderProcessor with Processor[Seq[fr.Datum[?]]] {

  override def processSite(site: Site): Seq[fr.Datum[?]] = Nil

}
