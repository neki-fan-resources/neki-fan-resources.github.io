package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.component.SocialMediaCard
import org.skyluc.html.*
import org.skyluc.neki_site.data.Member
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.data.SocialMedia
import org.skyluc.neki_site.html.NewsBlock
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

import Html.*

class BandPage(site: Site, description: PageDescription) extends SitePage(description, site) {

  import BandPage._

  override def javascriptFiles(): Seq[Url] = super.javascriptFiles() :+ Url(SitePage.SRC_NEWS_JAVASCRIPT)

  override def elementContent(): Seq[BodyElement[?]] = {
    List(
      div()
        .withClass(CLASS_BAND_NAME)
        .appendElements(
          img().withClass(CLASS_BAND_NAME_LOGO).withSrc(Url(URL_LOGO).toString()).withAlt("NEK! logo"),
          div().withClass(CLASS_BAND_NAME_SUBLABEL).appendElements(text(TEXT_SUBLABEL)),
          div()
            .withClass(CLASS_BAND_DESCRIPTION)
            .appendElements(
              p().appendElements(
                text("NEK! is a young slang rock band formed in February 2024.")
              ),
              p().appendElements(
                text("They are: Hika (vocals, guitar), Natsu (guitar), Kanade (bass) and Cocoro (Drums).")
              ),
              p().appendElements(
                text("They released their first single in April 2024, and their first EP in July 2024.")
              ),
              p().appendElements(
                text(
                  "Now in their second year, they released a second EP, did a 3 cities tour in Japan, and prepare for their first Zepp concert in August, at the Zepp Shinjuku in Tokyo."
                )
              ),
            ),
        )
        .appendElements(NewsBlock.generate(site.news)*),
      bandPanel(),
      div().withClass(CLASS_BAND_SOCIALS).appendElements(socials(site.band.socialMedia)*),
    )
  }

  private def bandPanel(): Div = {
    val elements = memberDivs("kanade", site.band.member.kanade) ::: memberDivs(
      "hika",
      site.band.member.hika,
    ) ::: memberDivs("cocoro", site.band.member.cocoro) ::: memberDivs("natsu", site.band.member.natsu)
    div()
      .withClass(CLASS_BAND_PANEL)
      .appendElements(
        elements*
      )
  }

  private def memberDivs(id: String, member: Member): List[Div] = {
    List(
      div()
        .withClass(CLASS_BAND_PANEL_MEMBER_INFO)
        .withClass(CLASS_BAND_PANEL_MEMBER_ + id)
        .withOnClick(toggleClassCode(member.id))
        .appendElements(
          socials(member.socialMedia)*
        ),
      div()
        .withClass(CLASS_BAND_PANEL_MEMBER_SUMMARY)
        .withClass(CLASS_BAND_PANEL_MEMBER_ + id)
        .withOnClick(toggleClassCode(member.id))
        .appendElements(
          div().withClass(CLASS_BAND_PANEL_MEMBER_SUMMARY_NAME).appendElements(text(member.name)),
          div().withClass(CLASS_BAND_PANEL_MEMBER_SUMMARY_ROLE).appendElements(text(member.role)),
        ),
    )
  }

  private def toggleClassCode(selector: String): String =
    s"""toggleClass('$CLASS_BAND_PANEL_MEMBER_$selector','$CLASS_BAND_PANEL_MEMBER_SELECTED')"""

  private def socials(socialMedia: SocialMedia): List[BodyElement[?]] = {
    List(
      socialMedia.instagram.map { handle =>
        SocialMediaCard.generate(handle, SOCIALMEDIA_BASE_URL_INSTAGRAM, "instagram", "instagram.ico")
      },
      socialMedia.youtube.map { handle =>
        SocialMediaCard.generate(handle, SOCIALMEDIA_BASE_URL_YOUTUBE, "YouTube", "youtube.svg")
      },
      socialMedia.tiktok.map { handle =>
        SocialMediaCard.generate(handle, SOCIALMEDIA_BASE_URL_TIKTOK, "TikTok", "tiktok.ico")
      },
      socialMedia.x.map { handle => SocialMediaCard.generate(handle, SOCIALMEDIA_BASE_URL_X, "X(Twitter)", "x.ico") },
    ).flatten
  }

}

object BandPage {

  val FILE_PATH = Path("index")
  val PAGE_PATH = Path()

  // TODO: common asset/image path somewhere
  val URL_LOGO_BASE = Path("asset", "image", "logo")
  val URL_LOGO = URL_LOGO_BASE.resolve("neki-tight.png")
  val URL_OG_IMAGE = Url(Path("asset", "image", "site", "band-getover-01.png"))

  val TEXT_SUBLABEL = """Read as "Neki""""

  // classes
  val CLASS_BAND_NAME = "band-name"
  val CLASS_BAND_NAME_LOGO = "band-name-logo"
  val CLASS_BAND_NAME_SUBLABEL = "band-name-sublabel"
  val CLASS_BAND_DESCRIPTION = "band-description"

  val CLASS_BAND_PANEL = "band-panel"
  val CLASS_BAND_PANEL_MEMBER_INFO = "band-panel-member-info"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY = "band-panel-member-summary"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY_NAME = "band-panel-member-summary-name"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY_ROLE = "band-panel-member-summary-role"
  val CLASS_BAND_PANEL_MEMBER_SELECTED = "band-panel-member-selected"
  val CLASS_BAND_PANEL_MEMBER_ = "band-panel-member-"

  val SOCIALMEDIA_BASE_URL_INSTAGRAM = "https://www.instagram.com/"
  val SOCIALMEDIA_BASE_URL_TIKTOK = "https://www.tiktok.com/@"
  val SOCIALMEDIA_BASE_URL_X = "https://x.com/"
  val SOCIALMEDIA_BASE_URL_YOUTUBE = "https://www.youtube.com/@"
  val CLASS_BAND_SOCIALS = "band-socials"

  def pages(site: Site): Seq[SitePage] = {
    val mainPage = BandPage(
      site,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Band",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Band",
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(URL_OG_IMAGE),
        SitePage.canonicalUrlFor(PAGE_PATH),
        FILE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
        true,
      ),
    )

    Seq(mainPage)
  }
}
