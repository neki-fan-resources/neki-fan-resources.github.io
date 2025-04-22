package org.skyluc.neki_site.checks

import org.skyluc.neki_site.data.Data

object CrossLinkData {

  def process(data: Data): Data = {
    data.all.values.foldLeft(data) { (d1, datum) =>
      datum.linkedTo.foldLeft(d1) { (d2, id) =>
        val linked = d2.all(id).withLinkedTo(datum.id)
        d2.copy(all = d2.all + ((linked.id, linked)))
      }
    }
  }
}
