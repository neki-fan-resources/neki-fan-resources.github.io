package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{CoverImage => dCoverImage}
import org.skyluc.fan_resources.data.FileCoverImage
import org.skyluc.fan_resources.data.AlbumCoverImage
import org.skyluc.fan_resources.data.TourCoverImage
import org.skyluc.fan_resources.data.SongCoverImage
import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.data.Datum

object CoverImage {

  def resolve(
      coverImage: dCoverImage,
      designation: String,
      name: String,
      item: Datum[?],
      compilers: Compilers,
  ): ImageCompiledData = {
    coverImage match {
      case FileCoverImage(filename, _) =>
        ImageCompiledData(
          resolveImageAsset(filename, item),
          buildAlt(designation, name),
          None,
        )
      case AlbumCoverImage(albumId) =>
        compilers.elementDataCompiler.get(albumId).cover
      case TourCoverImage(tourId) =>
        compilers.elementDataCompiler.get(tourId).cover
      case SongCoverImage(songId) =>
        compilers.elementDataCompiler.get(songId).cover
    }
  }

  // TODO: move in common
  def resolveImageAsset(filename: String, item: Datum[?]): Url = {
    Url(BASE_IMAGE_ASSET_PATH.resolve(item.id.path).resolve(filename))
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

  val BASE_IMAGE_ASSET_PATH = Path("asset", "image")
}
