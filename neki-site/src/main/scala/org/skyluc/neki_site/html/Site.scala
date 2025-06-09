package org.skyluc.neki_site.html

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.LocalImageCopyCompiledData
import org.skyluc.fan_resources.html.Url

object Site {
  val BASE_IMAGE_ASSET_PATH = Path("asset", "image")

  val DEFAULT_COVER_IMAGE = LocalImageCopyCompiledData(
    Url(BASE_IMAGE_ASSET_PATH.resolve(Path("manekineko-512px.png"))),
    "NEK! fan resources logo",
  )

  val MISSING_IMAGE = LocalImageCopyCompiledData(
    Url(BASE_IMAGE_ASSET_PATH.resolve("site").resolve(Path("manekineko-200px.png"))),
    Common.MISSING,
  )

  def resolveImageAsset(filename: String, item: Datum[?]): Url = {
    Url(BASE_IMAGE_ASSET_PATH.resolve(item.id.path).resolve(filename))
  }
}
