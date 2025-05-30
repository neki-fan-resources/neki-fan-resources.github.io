package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.Common

object Site {
  val BASE_IMAGE_ASSET_PATH = Path("asset", "image")

  val DEFAULT_COVER_IMAGE = ImageCompiledData(
    Url(BASE_IMAGE_ASSET_PATH.resolve(Path("manekineko-512px.png"))),
    "NEK! fan resources logo",
    Some(Url("/")), // TODO: extract ?
  )

  val MISSING_IMAGE = ImageCompiledData(
    Url(BASE_IMAGE_ASSET_PATH.resolve("site").resolve(Path("manekineko-200px.png"))),
    Common.MISSING,
    None,
  )

  def resolveImageAsset(filename: String, item: Datum[?]): Url = {
    Url(BASE_IMAGE_ASSET_PATH.resolve(item.id.path).resolve(filename))
  }
}
