package org.skyluc.neki_site.html

import org.skyluc.fan_resources.{html => fr}
import fr.component.{Head => HeadComponents}
import org.skyluc.fan_resources.data.Path
import org.skyluc.html._
import fr.component.OpenGraphSection
import fr.Url
import org.skyluc.neki_site.Config
import org.skyluc.neki_site.html.component.NavigationBar
import org.skyluc.neki_site.html.component.Footer
import fr.component.ExtraSection
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Id
import org.skyluc.html.Html.script

abstract class SitePage(override val description: PageDescription, compilers: Compilers) extends fr.SitePage {

  import SitePage._

  // override val description: PageDescription

  def elementContent(): Seq[BodyElement[?]]

  override def headContent(): Seq[HeadElement[?]] = {
    Seq(
      HeadComponents.charsetUtf8,
      HeadComponents.googleFonts(HREF_GOOGLE_FONT_NOTO),
      if (description.isRoot) {
        HeadComponents.searchEngineVerification(GOOGLE_VERIFICATION_CODE, MICROSOFT_VERIFICATION_CODE)
      } else { Nil },
      if (description.isDark) {
        HeadComponents.css(CSS_PATH, CSS_DARK_PATH)
      } else {
        HeadComponents.css(CSS_PATH)
      },
      HeadComponents.icons(HREF_ICON_512),
      OpenGraphSection.generate(description),
      HeadComponents.statistics(Config.current.isLocal, DOMAIN_NAME_NIFR),
    ).flatten
  }

  override def headerContent(): Seq[BodyElement[?]] =
    NavigationBar.generate(compilers.data.site.navigation, outputPath)

  override def mainContent(): Seq[BodyElement[?]] = {
    val extraSection = description.extraPage.map(ExtraSection.generate(_)).getOrElse(Nil)
    val scripts = script().withSrc(SRC_JAVASCRIPT)
    Seq(scripts)
      ++ elementContent()
      ++ extraSection
  }

  override def footerContent(): Seq[BodyElement[?]] =
    Footer.generate(description.oppositePage)

}

// abstract class DatumPage(compilers: Compilers) extends SitePage(compilers) {

//   override val description: PageDescription // = DescriptionGenerator.generate(datum)

//   // override val outputPath: Path = OutputPathResolver.resolve(datum)

// }

object SitePage {

  val HREF_GOOGLE_FONT_NOTO = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@100..900&display=swap"

  val CSS_PATH = Url(Path("asset", "css", "styles.css"))
  val CSS_DARK_PATH = Url(Path("asset", "css", "dark.css"))

  val HREF_ICON_512 = Url(Path("manekineko-512px.png"))
  val DOMAIN_NAME_NIFR = "neki-fan-resources.github.io"

  val GOOGLE_VERIFICATION_CODE = "DrE-ZbcyBV3lPatFCBja2O4ymKzfqFXDZjkfkTpvY_8"
  val MICROSOFT_VERIFICATION_CODE = "B6C2BBE1BBDED01F740330EB10DEAEF8"

  // javascript
  val SRC_JAVASCRIPT = "/asset/javascript/main.js"

  val DARK_PATH = Path("dark")

  def urlFor(path: Path): Url = Url(path.withExtension(Common.HTML_EXTENSION))

  def canonicalUrlFor(path: Path): Url = Url(Config.current.baseUrl, path.withExtension(Common.HTML_EXTENSION))

  def pageAndOppositePagePath(
      id: Id[?],
      oppositeId: Id[?],
      dark: Boolean,
      compilers: Compilers,
  ): (Path, Option[Path]) = {
    val oppositeDatum = compilers.data.all.get(oppositeId)

    if (dark) {
      (DARK_PATH.resolve(id.path), oppositeDatum.map(_ => id.path))
    } else {
      (id.path, oppositeDatum.map(_ => DARK_PATH.resolve(id.path)))
    }
  }

}
