package org.skyluc.neki.html

import org.skyluc.neki.data.{CoverImage => dCoverImage}
import org.skyluc.neki.data.Data
import org.skyluc.neki.data.Item
import org.skyluc.neki.data.FileCoverImage
import org.skyluc.neki.data.Song

object CoverImage {

  def resolveUrl(coverImage: dCoverImage, item: Item[?], data: Data): String = {
    coverImage match {
      case FileCoverImage(filename, _) =>
        resolveImageAsset(filename, item)
    }
  }

  def resolveImageAsset(filename: String, item: Item[?]): String = {
    BASE_IMAGE_ASSET_SONG + item.id.upath + filename
  }

  def buildAlt(designation: String, name: String): String = {
    val builder = new StringBuilder()
    builder
      .append(designation)
      .append(ALT_TEXT_1)
      .append(name)
      .append(ALT_TEXT_2)
    builder.toString()
  }

  // -----------------

  val ALT_TEXT_1 = " "
  val ALT_TEXT_2 = " cover image"

  val BASE_IMAGE_ASSET_SONG = "/asset/image/"
}