package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data.*

case class Data(
    site: Site,
    all: Map[Id[?], Datum[?]],
) {
  def get[T <: Datum[T]](id: Id[T]): T = {
    all(id).asInstanceOf[T]
  }

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
