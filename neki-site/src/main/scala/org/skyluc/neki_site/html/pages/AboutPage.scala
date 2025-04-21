package org.skyluc.neki_site.html.pages

import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.html.component.Defaults
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.Common
import org.skyluc.html._
import Html._
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.fan_resources.html.component.SocialMediaCard
import org.skyluc.neki_site.html.TitleAndDescription

class AboutPage(description: PageDescription, compilers: Compilers) extends SitePage(description, compilers) {

  import AboutPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    List(
      SectionHeader.generate("What is this site"),
      site(),
    ) :::
      manekineko() ::: List(
        SectionHeader.generate("Updates"),
        update(),
      ) ::: questions()
  }

  private def site(): Div = {
    div()
      .withClass(CLASS_ABOUT_SECTION)
      .appendElements(
        p().appendElements(text("This site was created with two main starting goals:")),
        ul().appendElements(
          li().appendElements(
            text(
              "Have a place to store details of the Japanese lyrics. My japanese is pretty poor, but I like to try to get a better understanding of the original lyrics, to go farther than the sometime so-so English version. I might as well share the resources I am using."
            )
          ),
          li().appendElements(
            text(
              "Provide a place with some information about the band. There is no main page for the band at this point. I found this quite frustrating when I started to look for information about NEK!."
            ),
            br(),
            text("So I started to add a bit of basic information about the band and their music"),
            br(),
            text("... then it got a bit out of hand 😁"),
          ),
        ),
      )
  }

  private def update(): Div = {
    div()
      .withClass(CLASS_ABOUT_SECTION)
      .appendElements(
        p().appendElements(
          text("Notification about content updates, and other general updates, are done at "),
          SocialMediaCard
            .generate("neki-fan-resources.github.io", "https://bsky.app/profile/", "bluesky", "bluesky.svg", true),
          text("."),
        )
      )

  }

  private def manekineko(): List[BodyElement[?]] = {
    List(
      // div().appendElements(
      div()
        .withClass(CLASS_ABOUT_MANEKINEKO)
        .appendElements(
          img()
            .withSrc(
              "/asset/image/site/manekineko-200px.png"
            )
        ),
      SectionHeader.generate("What about the 🐈 ?"),
      div()
        .withClass(CLASS_ABOUT_SECTION)
        .appendElements(
          p().appendElements(
            text("The starting idea for the logo comes from a word play in the lyrics of MAZE. ")
          ),
          p().appendElements(
            text("In the chorus, there is the line '"),
            spanLyrics("我らは魔NEK!猫"),
            text("'. The official translation just says '"),
            spanLyrics("We are the \"MA NEK! NEKO\""),
            text("', keeping the second part in phonetic ("),
            spanLyrics("manekineko"),
            text("). If translated from the kanji, it would rougthly be '"),
            spanLyrics("demon cat NEK!"),
            text("'."),
          ),
          p().appendElements(
            text("But a more common Japanese word with the "),
            spanLyrics("manekineko"),
            text(" sound is '"),
            spanLyrics("招き猫"),
            text("': the '"),
            spanLyrics("beckoming cat"),
            text("' which can be seen at the entrance of a lot of shops."),
          ),
          p().appendElements(
            text(
              "Keeping the phonetic version in the translation is likely to put the emphasis on the word play."
            )
          ),
          p().appendElements(
            text(
              "A bit of image generation, a touch of color for each band member, much cleanup, and we have a logo."
            )
          ),
        ),
      // )
    )
  }

  private def spanLyrics(t: String): Span = {
    span().withClass(CLASS_ABOUT_LYRICS_MANEKINEKO).appendElement(text(t))
  }

  private def questions(): List[BodyElement[?]] = {
    List(
      a().withName("questions"),
      SectionHeader.generate("Questions - Suggestions - Requests - Contact"),
      div()
        .withClass(CLASS_ABOUT_SECTION)
        .appendElements(
          p().appendElements(
            text("For any questions, suggestions, requests, please use "),
            SocialMediaCard
              .generate("neki-fan-resources.github.io", "https://bsky.app/profile/", "bluesky", "bluesky.svg", true),
            text(" or the "),
            SocialMediaCard.generate(
              "neki-fan-resources.github.io/issues",
              "https://github.com/neki-fan-resources/",
              "GitHub",
              "github.svg",
              true,
            ),
            text("."),
          )
        ),
    )
  }

}

object AboutPage {

  // classes
  val CLASS_ABOUT_MANEKINEKO = "about-manekineko"
  val CLASS_ABOUT_LYRICS_MANEKINEKO = "about-lyrics-manekineko"
  val CLASS_ABOUT_SECTION = "about-section"

  val PAGE_PATH = Path("about")

  def pages(compilers: Compilers): Seq[SitePage] = {
    val mainPage = AboutPage(
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "About",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "About",
          None,
          None,
          None,
        ),
        Defaults.COVER_IMAGE.source,
        SitePage.canonicalUrlFor(PAGE_PATH),
        PAGE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      compilers,
    )

    Seq(mainPage)
  }
}
