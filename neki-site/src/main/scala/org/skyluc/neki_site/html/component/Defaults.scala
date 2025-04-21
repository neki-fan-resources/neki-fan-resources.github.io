package org.skyluc.neki_site.html.component

import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.neki_site.html.CoverImage
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.Url

object Defaults {
  val COVER_IMAGE = ImageCompiledData(
    Url(CoverImage.BASE_IMAGE_ASSET_PATH.resolve(Path("manekineko-512px.png"))),
    "NEK! fan resources logo",
    Some(Url("/")), // TODO: extract ?
  )
}
