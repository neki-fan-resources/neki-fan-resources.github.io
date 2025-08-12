package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.GenImageCompiledData
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
    Seq(
      SectionHeader.generate("Update 2025-08-12"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                GenImageCompiledData(Url(image6Path), "update 2025-08-12")
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("New song \""),
                a().withHref("/song/ripoff.html").appendElements(text("rip-off")),
                text(
                  "\" released. Lyrics and live videos are now available."
                ),
              ),
              p().appendElements(
                text("In the lead-up for the "),
                a()
                  .withHref("/show/2025/1stzepponeman.html")
                  .appendElements(text("NEK! 1ST ZEPP ONE MAN")),
                text(
                  ", each member is answering a few questions. We now have English-subtitled versions of the Q&As."
                ),
              ),
              p().appendElements(
                text("More videos are available from their latest shows: "),
                a()
                  .withHref("/show/2025/internetbabies.html")
                  .appendElements(text("「INTERNET BaBIES -Face to Faith-」")),
                text(" and "),
                a()
                  .withHref("/show/2025/nekikomsumeosaka.html")
                  .appendElements(text("TWO-MAN SHOW with komsume")),
                text("."),
              ),
              p().appendElements(
                text("More "),
                a()
                  .withHref("/shows.html")
                  .appendElements(text("shows")),
                text(
                  " have also been announced."
                ),
              ),
            ),
        ),
      SectionHeader.generate("Update 2025-07-28"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                GenImageCompiledData(Url(image5Path), "update 2025-07-15")
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("New song \""),
                a().withHref("/song/ripoff.html").appendElements(text("rip-off")),
                text(
                  "\" to be released next week, on the 6th."
                ),
              ),
              p().appendElements(
                text("English translation for "),
                a().withHref("/song/fool.html").appendElements(text("Fool")),
                text(
                  ", from the Live Official Video."
                ),
              ),
              p().appendElements(
                text("They performed at "),
                a()
                  .withHref("/show/2025/internetbabies.html")
                  .appendElements(text("「INTERNET BaBIES -Face to Faith-」")),
                text(" last Sunday. Some photos and videos from the show are already available."),
              ),
              p().appendElements(
                text("Getting ready for the "),
                a()
                  .withHref("/show/2025/1stzepponeman.html")
                  .appendElements(text("1ST ZEPP ONE MAN")),
                text(
                  ". This week is Cocoro's week. The first video is available."
                ),
              ),
            ),
        ),
      SectionHeader.generate("Update 2025-07-15"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                GenImageCompiledData(Url(image4Path), "update 2025-07-15")
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("Live Official Video for "),
                a().withHref("/song/fool.html").appendElements(text("Fool")),
                text(
                  " released."
                ),
              ),
              p().appendElements(
                text("On Friday July 18th, 3 radio appearances on "),
                a()
                  .withHref("/media/2025/radiokansai.html")
                  .appendElements(text("ラジオ出演 (radio Kansai)")),
                text(", "),
                a()
                  .withHref("/media/2025/kissfmkobe.html")
                  .appendElements(text("Kiss FM")),
                text(", and "),
                a()
                  .withHref("/media/2025/fmosaka.html")
                  .appendElements(text("FM Osaka")),
                text(", in Kobe and Osaka. Then the "),
                a()
                  .withHref("/show/2025/nekikomsumeosaka.html")
                  .appendElements(text("TWO-MAN SHOW with komsume")),
                text(", at 	LIVE SQUARE 2nd LINE."),
              ),
              p().appendElements(
                text("And NEK! will be performing this year again at "),
                a()
                  .withHref("/show/2025/minamiwheel.html")
                  .appendElements(text("MINAMI WHEEL")),
                text(
                  "."
                ),
              ),
            ),
        ),
      SectionHeader.generate("Update 2025-06-20"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                GenImageCompiledData(Url(image3Path), "update 2025-06-20")
              )
            ),
          div()
            .withClass(CLASS_UPDATE_CONTENT)
            .appendElements(
              p().appendElements(
                text("More "),
                a().withHref("/song/ooak.html").appendElements(text("OOKA")),
                text(
                  " content is available. The official music video, english translation of the lyrics, playthrough sections by Natsu."
                ),
              ),
              p().appendElements(
                text("3 new shows have been announced: at the  "),
                a()
                  .withHref("/show/2025/sendaimusicfuse.html")
                  .appendElements(text("Sendai music Fuse")),
                text(" festival, at Hamane Haruki's "),
                a()
                  .withHref("/show/2025/internetbabies.html")
                  .appendElements(text("「INTERNET BaBIES -Face to Faith-」")),
                text(" show, and at the "),
                a()
                  .withHref("/show/2025/thetheoryofscience.html")
                  .appendElements(text("The Theory of SCIENCE")),
                text(" festival."),
              ),
              p().appendElements(
                text("Radio appearances of Hika and Natsu on "),
                a()
                  .withHref("/media/2025/yfmgodblesssaturday.html")
                  .appendElements(text("Fm yokohama")),
                text(
                  " and "
                ),
                a()
                  .withHref("/media/2025/eradiocatch.html")
                  .appendElements(text("e-radio")),
                text(
                  " were announced."
                ),
              ),
            ),
        ),
      SectionHeader.generate("Update 2025-06-06"),
      div()
        .withClass(CLASS_UPDATES_SECTION)
        .appendElements(
          div()
            .withClass(CLASS_UPDATE_IMAGE)
            .appendElements(
              LargeImageCover.generate(
                GenImageCompiledData(Url(image2Path), "update 2025-06-06")
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
                GenImageCompiledData(
                  Url(image1Path),
                  "update 2025-05-20",
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
  val image1Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250520.png")
  val image2Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250606.png")
  val image3Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250620.png")
  val image4Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250715.png")
  val image5Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250728.png")
  val image6Path = Site.BASE_IMAGE_ASSET_PATH.resolve("updates", "updates-20250812.png")

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
        SitePage.absoluteUrl(Url(image6Path)),
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
