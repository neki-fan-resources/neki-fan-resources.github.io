package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data.{Data as frData, *}

case class Data(
    site: Site,
    all: Map[Id[?], Datum[?]],
) extends frData {

  def withDatums(datums: Iterable[Datum[?]]): Data = {
    Data(site, datums.map(d => (d.id, d)).toMap)
  }
}

object Data {
  def apply(datums: Seq[Datum[?]]): Data = {
    val all = datums.map(d => (d.id, d)).toMap
    val site = all(Site.ID).asInstanceOf[Site]
    Data(site, all)
  }
}
