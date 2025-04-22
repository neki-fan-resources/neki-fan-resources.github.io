package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Id
import org.skyluc.neki_site.data.Data

case class CheckError(id: Id[?], errorMessage: String) extends BaseError {
  override def toString(): String = s"$id: $errorMessage"
}

object DataCheck {

  case class Acc(errors: Seq[CheckError], datums: List[Datum[?]])

  object Acc {
    val EMPTY = Acc(Nil, Nil)
  }

  def check(data: Data): (Seq[CheckError], Data) = {

    val populatedData = PopulateRelatedTo.process(data)

    val (linkedErrors, linkCheckedData) = CheckLinkedToExists.process(populatedData)

    val (assetErrors, assetCheckedData) = CheckLocalAssetExists.process(linkCheckedData)

    val crossLinkedData = CrossLinkData.process(assetCheckedData)

    (linkedErrors ++ assetErrors, crossLinkedData)

  }
}
