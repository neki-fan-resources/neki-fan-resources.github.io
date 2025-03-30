package org.skyluc.neki.html

case class ItemCompiledData(
  designation: String,
  label: String,
  sublabel: Option[String],
  coverUrl: String,
  coverAlt: String,
  info: List[ItemInfo],
)

case class ItemInfo(
  label: Option[String],
  value: String
)

object ItemInfo {
  def apply(label: String, value: String): ItemInfo =
    ItemInfo(Some(label), value)
}

object CompiledData {

  val LABEL_RELEASED = "released"

}