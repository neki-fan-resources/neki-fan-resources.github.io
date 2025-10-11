package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*

import Html.*

object AboutPage extends MainSitePage {

  val CLASS_ABOUT_MANEKINEKO = "about-manekineko"
  val CLASS_ABOUT_LYRICS_MANEKINEKO = "about-lyrics-manekineko"
  val CLASS_ABOUT_SECTION = "about-section"

  override val outputPath: Path = Path("about.html")

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle("About"),
      TitleGenerator.generateTitle("About"),
      outputPath.toAbsoluteString(),
      MainSitePage.imageLogo.imageUrl,
    )

  override def mainContent(): Seq[BodyElement[?]] = {

    val sections: Seq[fr.page.ContentSection] = Seq(
      SiteSection,
      ManekinekoSection,
      UpdatesSection,
      SupportSection,
      QuestionsSection,
    )

    sections.flatMap(_.content())
  }

  object SiteSection extends fr.page.ContentSection {

    override val label: String = "What is this site?"

    override def mainContent(): BodyElement[?] = {
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

  }

  object ManekinekoSection extends fr.page.ContentSection {

    override val label: String = "What about the 🐈 ?"

    override def customContent(): Seq[BodyElement[?]] = {
      Seq(
        div()
          .withClass(CLASS_ABOUT_MANEKINEKO)
          .appendElements(
            img()
              .withSrc(
                "/asset/image/site/manekineko-200px.png"
              )
          )
      )
    }

    override def mainContent(): BodyElement[?] = {
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
            spanLyrics("beckoning cat"),
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
        )
    }

    private def spanLyrics(t: String): Span = {
      span().withClass(CLASS_ABOUT_LYRICS_MANEKINEKO).appendElement(text(t))
    }

  }

  object UpdatesSection extends fr.page.ContentSection {

    override val label: String = "Updates"

    override def mainContent(): BodyElement[?] = {
      div()
        .withClass(CLASS_ABOUT_SECTION)
        .appendElements(
          p().appendElements(
            text("Notification about content updates, and other general updates, are done at "),
            fr.component.SocialMediaCard.generateBluesky("neki.fan-resources.net", true),
            text(" and "),
            fr.component.SocialMediaCard.generateX("neki.fan-resources.net", true),
            text("."),
          ),
          p().appendElements(
            text("All the content additions are listed on the "),
            a().withHref("/updates.html").appendElements(text("Update page")),
            text("."),
          ),
        )
    }

  }

  object SupportSection extends fr.page.ContentSection {

    override val label: String = "Support"

    override def anchor(): Option[String] = Some("support")

    override def mainContent(): BodyElement[?] = {
      div()
        .withClass(CLASS_ABOUT_SECTION)
        .appendElements(
          p().appendElements(
            text(
              "While I'm doing the work because I like NEK! and want to support them, it does take a fair bit of time and resources to create, improve, and update the site."
            )
          ),
          p().appendElements(
            text(
              "Any level of support will be greatly appreciated. You can use the Kofi Fan Resources page, and don't forget to indicate in the message that you're a NEK! fan."
            )
          ),
          fr.component.Kofi.generateBadge(),
        )
        .appendElements(supporters()*)
    }

  }

  private def supporters(): Seq[BodyElement[?]] = Seq(
    ul().appendElements(
      li().appendElements(
        text("NEK! supporters"),
        ul().appendElements(
          li().appendElements(text("SkyLuc"))
        ),
      ),
      li().appendElements(
        text("Fan Resources network"),
        ul().appendElements(
          li().appendElements(text("mab21")),
          li().appendElements(text("flomdo")),
          li().appendElements(text("ombe_toul")),
        ),
      ),
    )
  )

  object QuestionsSection extends fr.page.ContentSection {

    override val label: String = "Questions - Suggestions - Requests - Contact"

    override def anchor(): Option[String] = Some("questions")

    override def mainContent(): BodyElement[?] = {
      div()
        .withClass(CLASS_ABOUT_SECTION)
        .appendElements(
          p().appendElements(
            text("For any questions, suggestions, requests, please use "),
            fr.component.SocialMediaCard.generateBluesky("neki.fan-resources.net", true),
            text(" or "),
            fr.component.SocialMediaCard.generateX("neki.fan-resources.net", true),
            text("."),
          )
        )
    }

  }

}
