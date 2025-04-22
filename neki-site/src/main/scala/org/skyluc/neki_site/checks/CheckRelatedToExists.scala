package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.data.Datum
import org.skyluc.neki_site.checks.DataCheck.Acc
import org.skyluc.neki_site.data.Data

object CheckLinkedToExists {

  def process(data: Data): (Seq[CheckError], Data) = {
    val all = data.all

    val res = all.values.foldLeft(Acc.EMPTY) { (acc, datum) =>
      val missingIds = datum.linkedTo.filterNot(all.contains(_))
      if (missingIds.isEmpty) {
        Acc(acc.errors, datum :: acc.datums)
      } else {
        val errors = missingIds.map { id =>
          CheckError(datum.id, s"referenced '$id' not found.")
        }
        Acc(errors ++ acc.errors, datum.errored() :: acc.datums)
      }
    }
    (res.errors, data.withDatums(res.datums))
  }

}
