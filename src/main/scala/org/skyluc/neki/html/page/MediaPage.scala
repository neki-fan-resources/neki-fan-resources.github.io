package org.skyluc.neki.html.page

import org.skyluc.neki.data.Media
import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html.BodyElement
import java.nio.file.Path
import org.skyluc.neki.html.ItemDetails
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.Pages
import org.skyluc.neki.html.CoverImage

class MediaPage(val media: Media, data: Data) extends Page(data) {
  import MediaPage._

  override def path(): Path = Path.of(MEDIA_PATH, media.id.year, media.id.id + Pages.HTML_EXTENSION)

  override def shortTitle(): String = media.radio + " - " + media.show + TITLE_DESIGNATION

  override def altName(): Option[String] = None

  override def ogImageUrl(): Option[String] = Some(CoverImage.resolveUrl(media.coverImage, media, data))

  override def mainContent(): List[BodyElement[?]] = {
    List(
      ItemDetails.generate(CompiledData.getMedia(media.id, data))
    )
  }

}

object MediaPage {
  val MEDIA_PATH = "media"

  val DESIGNATION = "Interview" // TODO: will need subtyping at some point
  val TITLE_DESIGNATION = " - " + DESIGNATION

  val LABEL_HOST = "host"
  val VALUE_SHOW_PAGE = "show page"
  val VALUE_PROGRAM = "program"

}
