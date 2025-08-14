package org.skyluc.neki_site.html

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Id
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*
import org.skyluc.neki_site.Config
import org.skyluc.neki_site.data.Site as dSite
import org.skyluc.neki_site.html.component.Footer
import org.skyluc.neki_site.html.component.NavigationBar

import fr.component.Head as HeadComponents
import fr.component.OpenGraphSection
import fr.Url
import fr.component.ExtraSection
import fr.CompiledDataGenerator

abstract class SitePage(override val description: PageDescription, site: dSite) extends fr.SitePage {

  import SitePage._

  def elementContent(): Seq[BodyElement[?]]

  override def javascriptFiles(): Seq[Url] = JAVASCRIPT_FILES

  override def headContent(): Seq[HeadElement[?]] = {
    Seq(
      HeadComponents.charsetUtf8,
      HeadComponents.googleFonts(HREF_GOOGLE_FONT_NOTO),
      if (description.isRoot) {
        HeadComponents.searchEngineVerification(GOOGLE_VERIFICATION_CODE, MICROSOFT_VERIFICATION_CODE)
      } else { Nil },
      if (description.isDark) {
        HeadComponents.css(CSS_PATH, CSS_FR_PATH, CSS_DARK_PATH)
      } else {
        HeadComponents.css(CSS_PATH, CSS_FR_PATH)
      },
      HeadComponents.icons(HREF_ICON_512),
      OpenGraphSection.generate(description),
      HeadComponents.statistics(Config.current.isLocal, DOMAIN_NAME_NIFR),
    ).flatten
  }

  override def headerContent(): Seq[BodyElement[?]] =
    NavigationBar.generate(site.navigation, outputPath)

  override def mainContent(): Seq[BodyElement[?]] = {
    val extraSection = description.extraPage.map(ExtraSection.generate(_)).getOrElse(Nil)
    elementContent()
      ++ extraSection
  }

  override def footerContent(): Seq[BodyElement[?]] =
    Footer.generate(description.oppositePage)

}

object SitePage {

  val HREF_GOOGLE_FONT_NOTO = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap"

  val CSS_PATH = Url(Path("asset", "css", "styles.css"))
  val CSS_FR_PATH = Url(Path("asset", "css", "styles-fr.css"))
  val CSS_DARK_PATH = Url(Path("asset", "css", "dark.css"))

  val HREF_ICON_512 = Url(Path("manekineko-512px.png"))
  val DOMAIN_NAME_NIFR = "neki.fan-resources.net"

  val GOOGLE_VERIFICATION_CODE = "5DUf4g9lYSa_jzwy0JIrwsfTppM2uM5culgvgkbXj7U"
  val MICROSOFT_VERIFICATION_CODE = "B6C2BBE1BBDED01F740330EB10DEAEF8"

  // javascript
  private val SRC_JAVASCRIPT = "/asset/javascript/main.js"
  val SRC_NEWS_JAVASCRIPT = "/asset/javascript/news.js"
  val SRC_OVERLAY_JAVASCRIPT = "/asset/javascript/overlay.js"
  val SRC_CONTENT_JAVASCRIPT = "/asset/javascript/content.js"
  val SRC_FRMAIN_JAVASCRIPT = "/asset/javascript/frmain.js"
  val JAVASCRIPT_FILES = Seq(Url(SRC_JAVASCRIPT))

  def urlFor(path: Path): Url = Url(path.withExtension(Common.HTML_EXTENSION))

  // TODO: this is quite ugly
  def absoluteUrl(url: Url): Url =
    if (url.text.startsWith("http")) {
      url
    } else {
      if (url.text.startsWith("/")) {
        Url(Config.current.baseUrl, url.text.substring(1))
      } else {
        Url(Config.current.baseUrl, url.text)
      }
    }

  def canonicalUrlFor(path: Path): Url = Url(Config.current.baseUrl, path.withExtension(Common.HTML_EXTENSION))

  def pageAndOppositePagePath(
      id: Id[?],
      oppositeId: Id[?],
      dark: Boolean,
      generator: CompiledDataGenerator,
  ): (Path, Option[Path]) = {
    pageAndOppositePagePath(id.path, id, oppositeId, dark, generator)
  }

  def pageAndOppositePagePath(
      basePath: Path,
      id: Id[?],
      oppositeId: Id[?],
      dark: Boolean,
      generator: CompiledDataGenerator,
  ): (Path, Option[Path]) = {
    val oppositeDatum = generator.getOption(oppositeId)

    if (dark) {
      (Id.DARK_PATH.resolve(basePath), oppositeDatum.map(_ => basePath))
    } else {
      (basePath, oppositeDatum.map(_ => Id.DARK_PATH.resolve(basePath)))
    }
  }

}
