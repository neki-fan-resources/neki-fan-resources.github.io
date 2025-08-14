package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

class ImplicitDatum extends fr.ImplicitDatum with Processor[Seq[fr.Datum[?]]] {

  override def processSite(site: Site): Seq[fr.Datum[?]] = Nil

}
