package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*
import org.skyluc.neki_site.Config

import Html.*
import fr.component.NavigationItem

object TitleGenerator extends fr.page.TitleGenerator {
  override def groupName: String = "NEK!"
  override def groupNameExt: String = "the band NEK!"
}

case class MainSitePageConfiguration(
    title: String,
    description: String,
    canonicalUrl: String,
    imageUrl: String,
    isRoot: Boolean = false,
) extends fr.page.MainSitePageConfiguration {

  override val headConfiguration: fr.component.HeadConfiguration =
    MainSitePage.headConfiguration(
      title,
      description,
      canonicalUrl,
      imageUrl,
      isRoot,
    )

}

abstract class MainSitePage extends fr.page.MainSitePage {

  import MainSitePage.*

  override def headerContent(): Seq[BodyElement[?]] = fr.component.Header.generate(
    MainSitePage.headerLogo,
    "NEK!",
    mainNavItems,
    supportNavItems,
    outputPath.firstSegment(),
  )

  override def footerContent(): Seq[BodyElement[?]] = fr.component.Footer.generate("the band NEK!")

  override def javascriptFiles(): Seq[Path] = fr.page.MainSitePage.JAVASCRIPT_FILES

}

object MainSitePage {

  def headConfiguration(
      title: String,
      description: String,
      canonicalUrl: String,
      imageUrl: String,
      isRoot: Boolean,
  ): fr.component.HeadConfiguration =
    fr.component.HeadConfiguration(
      DOMAIN_NAME,
      title,
      description,
      Config.current.baseUrl + canonicalUrl,
      Config.current.baseUrl + imageUrl,
      Config.current.baseUrl + MainSitePage.imageLogo.imageUrl,
      CSS_PATHS,
      GOOGLE_VERIFICATION_CODE,
      MICROSOFT_VERIFICATION_CODE,
      PLAUSIBLE_SCRIPT_ID,
      HREF_FONT_NOTO,
      isRoot,
      Config.current.isLocal,
    )

  val DOMAIN_NAME = "neki.fan-resources.net"
  val CSS_PATHS =
    Seq(Path("asset", "css", "fr-styles.css"), Path("asset", "css", "styles.css"))

  val headerLogo = fr.compileddata.MultimediaCompiledData(
    "image",
    "logo",
    Path("asset", "image", "site", "manekineko-200px.png").toAbsoluteString(),
    "NEK! Fan Resources logo",
    Common.MISSING_URL,
    Common.MISSING_DATE,
    "Designed for the site",
    true,
    None,
    Nil,
    fr.compileddata.Overlay(Common.MISSING_URL, Common.MISSING, false),
  )

  val imageLogo = fr.compileddata.MultimediaCompiledData(
    "image",
    "logo",
    Path("manekineko-512px.png").toAbsoluteString(),
    "NEK! Fan Resources logo",
    Common.MISSING_URL,
    Common.MISSING_DATE,
    "Designed for the site",
    true,
    None,
    Nil,
    fr.compileddata.Overlay(Common.MISSING_URL, Common.MISSING, false),
  )

  val HREF_FONT_NOTO = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap"

  val GOOGLE_VERIFICATION_CODE = "5DUf4g9lYSa_jzwy0JIrwsfTppM2uM5culgvgkbXj7U"
  val MICROSOFT_VERIFICATION_CODE = "B6C2BBE1BBDED01F740330EB10DEAEF8"
  val PLAUSIBLE_SCRIPT_ID = "UtUVlnWHE1BKmZqHXkwtl"

  val mainNavItems = Seq(
    NavigationItem(
      "Band",
      "/",
      List(),
    ),
    NavigationItem(
      "Music",
      "/music.html",
      List("music.html", "song", "album"),
    ),
    NavigationItem(
      "Shows",
      "/shows.html",
      List("shows.html", "show", "tour"),
    ),
    NavigationItem(
      "Media",
      "/medias.html",
      List("medias.html", "media"),
    ),
    NavigationItem(
      "Chronology",
      "/chronology.html",
      List("chronology.html"),
    ),
    NavigationItem(
      "Community",
      "/community.html",
      List("community.html"),
    ),
  )

  val supportNavItems = Seq(
    NavigationItem(
      "Updates",
      "/updates.html",
      List("updates.html"),
    ),
    NavigationItem(
      "Sources",
      "/sources.html",
      List("sources.html"),
    ),
    NavigationItem(
      "About",
      "/about.html",
      List("about.html"),
    ),
  )

}
