package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html._
import Html._
import java.nio.file.Path
import org.skyluc.neki.data.SocialMedia
import org.skyluc.neki.data.Member
import org.skyluc.neki.html.SocialMediaCard

class BandPage(data: Data) extends Page(data) {

  import BandPage._

  override val isRoot: Boolean = true

  override def path(): Path = Path.of("index.html")

  override def url() = Path.of("")

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def ogImageUrl(): Option[String] = Some("/asset/image/site/band-getover-01.png")

  override def mainContent(): List[BodyElement[?]] = {
    List(
      div()
        .withClass(CLASS_BAND_NAME)
        .appendElements(
          img().withClass(CLASS_BAND_NAME_LOGO).withSrc(URL_LOGO).withAlt("NEK! logo"),
          div().withClass(CLASS_BAND_NAME_SUBLABEL).appendElements(text(TEXT_SUBLABEL)),
        ),
      bandPanel(),
      div().withClass(CLASS_BAND_SOCIALS).appendElements(socials(data.site.band.socialMedia)*),
    )
  }

  private def bandPanel(): Div = {
    val elements = memberDivs("kanade", data.site.band.member.kanade) ::: memberDivs(
      "hika",
      data.site.band.member.hika,
    ) ::: memberDivs("cocoro", data.site.band.member.cocoro) ::: memberDivs("natsu", data.site.band.member.natsu)
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
  val DESIGNATION = "Band"

  val URL_LOGO_BASE = "/asset/image/logo/"
  val URL_LOGO = URL_LOGO_BASE + "neki.png"

  val CLASS_BAND_NAME = "band-name"
  val CLASS_BAND_NAME_LOGO = "band-name-logo"
  val CLASS_BAND_NAME_SUBLABEL = "band-name-sublabel"

  val CLASS_BAND_PANEL = "band-panel"
  val CLASS_BAND_PANEL_MEMBER_INFO = "band-panel-member-info"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY = "band-panel-member-summary"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY_NAME = "band-panel-member-summary-name"
  val CLASS_BAND_PANEL_MEMBER_SUMMARY_ROLE = "band-panel-member-summary-role"
  val CLASS_BAND_PANEL_MEMBER_SELECTED = "band-panel-member-selected"
  val CLASS_BAND_PANEL_MEMBER_ = "band-panel-member-"

  val CLASS_BAND_SOCIALS = "band-socials"

  val TEXT_SUBLABEL = """Read as "Neki""""

  val SOCIALMEDIA_LOGO_ALT = " logo"
  val SOCIALMEDIA_BASE_URL_INSTAGRAM = "https://www.instagram.com/"
  val SOCIALMEDIA_BASE_URL_TIKTOK = "https://www.tiktok.com/@"
  val SOCIALMEDIA_BASE_URL_X = "https://x.com/"
  val SOCIALMEDIA_BASE_URL_YOUTUBE = "https://www.youtube.com/@"

}
