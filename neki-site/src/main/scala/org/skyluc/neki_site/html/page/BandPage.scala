package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data.NewsItem
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*
import org.skyluc.neki_site.data.Member
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data.SocialMedia

import Html.*

class BandPage(site: Site, newsItems: Seq[fr.compileddata.NewsItemCompiledData]) extends MainSitePage {

  import BandPage.*

  override val outputPath: Path = Path("index.html")

  override def javascriptFiles(): Seq[Path] = super.javascriptFiles() :+ fr.page.MainSitePage.JAVASCRIPT_NEWS_FILE

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle("Band"),
      TitleGenerator.generateDescription("Band"),
      "/",
      URL_IMAGE_PATH.toAbsoluteString(),
      true,
    )

  override def mainContent(): Seq[BodyElement[?]] = {
    List(
      div()
        .withClass(CLASS_BAND_NAME)
        .appendElements(
          img().withClass(CLASS_BAND_NAME_LOGO).withSrc(URL_LOGO).withAlt("NEK! logo"),
          div().withClass(CLASS_BAND_NAME_SUBLABEL).appendElements(text(TEXT_SUBLABEL)),
          div()
            .withClass(CLASS_BAND_DESCRIPTION)
            .appendElements(
              p().appendElements(
                text("NEK! is a young \"slang rock\" band formed in February 2024.")
              ),
              p().appendElements(
                text("The members are Hika (vocals, guitar), Natsu (guitar), Kanade (bass) and Cocoro (Drums).")
              ),
              p().appendElements(
                text("They released their first single in April 2024, followed by their first EP in July 2024.")
              ),
              p().appendElements(
                text(
                  "Now in their second year, the band has released a second EP, completed a three-cities tour in Japan, and held their first Zepp concert in August at Zepp Shinjuku in Tokyo."
                )
              ),
              p().appendElements(
                text(
                  "NEK! will release their first full album on November 26th, with a nation-wide tour already scheduled for early next year."
                )
              ),
            ),
        )
        .appendElements(fr.component.NewsBlock.generate(newsItems)*),
      bandPanel2(),
      div().withClass(CLASS_BAND_SOCIALS).appendElements(socials(site.band.socialMedia)*),
    )
  }

  private def bandPanel2(): Div = {
    val members =
      Seq(
        memberDivs2("kanade", site.band.member.kanade),
        memberDivs2("hika", site.band.member.hika),
        memberDivs2("cocoro", site.band.member.cocoro),
        memberDivs2("natsu", site.band.member.natsu),
      ).flatten
    val central =
      div()
        .withClass(CLASS_BAND_PANEL2_CENTRAL)
        .withClass(CLASS_BAND_PANEL2_SECTION)
        .appendElements(
          img().withSrc("/asset/image/site/band.png")
        )
    div()
      .withClass(CLASS_BAND_PANEL2)
      .appendElements(central)
      .appendElements(members*)
  }

  private def memberDivs2(id: String, member: Member): Seq[Div] = {
    Seq(
      div()
        .withClass(CLASS_BAND_PANEL2_MEMBER_ + id)
        .withClass(CLASS_BAND_PANEL2_SECTION)
        .appendElements(
          img().withSrc("/asset/image/site/visual-" + id + ".png")
        ),
      div()
        .withClass(CLASS_BAND_PANEL2_MEMBER_ + id)
        .withClass(CLASS_BAND_PANEL2_MEMBER_INFO)
        .withClass(CLASS_BAND_PANEL2_MEMBER_INFO_HIDDEN)
        .withOnClick(toggleClassCode(member.id))
        .appendElements(
          socials(member.socialMedia)*
        ),
      div()
        .withClass(CLASS_BAND_PANEL2_MEMBER_ + id)
        .withClass(CLASS_BAND_PANEL2_MEMBER_SUMMARY)
        .appendElements(
          div()
            .withClass(CLASS_BAND_PANEL2_MEMBER_SUMMARY_NAME)
            .appendElements(text(member.name)),
          div()
            .withClass(CLASS_BAND_PANEL2_MEMBER_SUMMARY_ROLE)
            .appendElements(text(member.role)),
        )
        .withOnClick(toggleClassCode(member.id)),
    )
  }

  private def toggleClassCode(selector: String): String =
    s"""toggleClass('$CLASS_BAND_PANEL2_MEMBER_$selector','$CLASS_BAND_PANEL2_MEMBER_INFO_HIDDEN')"""

  private def socials(socialMedia: SocialMedia): List[BodyElement[?]] = {
    List(
      socialMedia.instagram.map { handle =>
        fr.component.SocialMediaCard.generateInstagram(handle)
      },
      socialMedia.youtube.map { handle =>
        fr.component.SocialMediaCard.generateYouTube(handle)
      },
      socialMedia.tiktok.map { handle =>
        fr.component.SocialMediaCard.generateTikTok(handle)
      },
      socialMedia.x.map { handle => fr.component.SocialMediaCard.generateX(handle) },
    ).flatten
  }

}

object BandPage {

  private val CLASS_BAND_NAME = "band-name"
  private val CLASS_BAND_NAME_LOGO = "band-name-logo"
  private val CLASS_BAND_NAME_SUBLABEL = "band-name-sublabel"
  private val CLASS_BAND_DESCRIPTION = "band-description"

  private val CLASS_BAND_PANEL2 = "band-panel2"
  private val CLASS_BAND_PANEL2_SECTION = "band-panel2-section"
  private val CLASS_BAND_PANEL2_CENTRAL = "band-panel2-central"
  private val CLASS_BAND_PANEL2_MEMBER_ = "band-panel2-member-"
  private val CLASS_BAND_PANEL2_MEMBER_INFO_HIDDEN = "band-panel2-member-info-hidden"
  private val CLASS_BAND_PANEL2_MEMBER_INFO = "band-panel2-member-info"
  private val CLASS_BAND_PANEL2_MEMBER_SUMMARY = "band-panel2-member-summary"
  private val CLASS_BAND_PANEL2_MEMBER_SUMMARY_NAME = "band-panel2-member-summary-name"
  private val CLASS_BAND_PANEL2_MEMBER_SUMMARY_ROLE = "band-panel2-member-summary-role"

  private val CLASS_BAND_SOCIALS = "band-socials"

  private val TEXT_SUBLABEL = """Read as "Neki""""

  private val URL_IMAGE_PATH = Path("asset", "image", "site", "band-getover-01.png")

  private val URL_LOGO = Path("asset", "image", "logo", "neki-tight.png").toAbsoluteString()

  def pagesFor(site: Site, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.MainSitePage] = {

    val newsItems =
      generator.getWithPrefix[NewsItem](NewsItem.ID_BASE_PATH).filter(_.active)

    val newsItemCompiledData =
      newsItems.map(fr.compileddata.RichTextCompiledData.toCompiledData(_, generator)).sortBy(_.element.date)

    Seq(
      BandPage(site, newsItemCompiledData)
    )
  }
}
