package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.component.LargeImageCover
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site as dSite
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*

class UpdatesPage(description: PageDescription, site: dSite) extends SitePage(description, site) {

  import UpdatesPage.*

  override def elementContent(): Seq[BodyElement[?]] = {

    val image1Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250520.png")
    val image2Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250606.png")
    Seq(
      SectionHeader.generate("Update 2025-06-06"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                ImageCompiledData(
                  Url(image2Path),
                  "update 2025-06-06",
                  Some(Url(image2Path)),
                )
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("New single released: "),
                a().withHref("/song/ooak.html").appendElements(text("OOKA")),
                text(". Lyrics are available, as well as previously shared videos."),
              ),
              p().appendElements(
                text("NEK! performed with komsume in a "),
                a()
                  .withHref("/show/2025/nekikomsumetokyo.html")
                  .appendElements(text("TWO-MAN SHOW")),
                text(", a few pictures and videos are available."),
              ),
              p().appendElements(
                text("Kanade published a "),
                a()
                  .withHref("/song/zerosum.html")
                  .appendElements(text("playthrough video for zero-sum")),
                text(
                  ". She also goes through the parts of the song, and gives pointers for people who want to play it."
                ),
              ),
            ),
        ),
      SectionHeader.generate("Update 2025-05-20"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                ImageCompiledData(
                  Url(image1Path),
                  "update 2025-05-20",
                  Some(Url(image1Path)),
                )
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("The band members have been sharing more videos on their personal accounts.")
              ),
              p().appendElements(
                text("There are several videos from "),
                a()
                  .withHref("/show/2025/kakumeilogic2025.html")
                  .appendElements(text("the 革命ロジック2025 (Kakumei Logic 2025) show")),
                text(", by each band member on stage, for different songs."),
              ),
              p().appendElements(
                text("They also shared "),
                a()
                  .withHref("/dark/song/newsong202505.html")
                  .appendElements(text("teasers")),
                text(" for a new song they are recording, from rehearsals and recording studio."),
              ),
            ),
        ),
    )
  }
}

object UpdatesPage {

  val PAGE_PATH = Path("updates")

  val CLASS_UPDATES_SECTION = "updates-section"
  val CLASS_UPDATE_IMAGE = "update-image"
  val CLASS_UPDATE_CONTENT = "update-content"

  // TODO: get the image from yml data
  val imagePath = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250520.png")

  def pages(site: dSite): Seq[SitePage] = {
    val mainPage = UpdatesPage(
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Updates",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Updates",
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(Url(imagePath)),
        SitePage.canonicalUrlFor(PAGE_PATH),
        PAGE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      site,
    )
    Seq(mainPage)
  }
}
