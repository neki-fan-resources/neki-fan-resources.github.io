package org.skyluc.neki_site.html.pages

import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.*
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class MusicPage(music: LayeredData[ElementCompiledData], pageDescription: PageDescription, site: d.Site)
    extends SitePage(pageDescription, site) {

  import MusicPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    List(
      MainIntro.generate(if (pageDescription.isDark) DARK_MAIN_INTRO_TEXT else MAIN_INTRO_TEXT),
      MediumCard.generateTree(music),
    )
  }

}

object MusicPage {

  val MAIN_INTRO_TEXT = "The albums and songs released by NEK!."

  val DARK_MAIN_INTRO_TEXT = "The songs played live by NEK!, but not announced or named yet."

  val PAGE_PATH = Path("music")

  def pagesFor(musicPage: d.MusicPage, site: d.Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val music: LayeredData[ElementCompiledData] = musicPage.music.map {
      case a: AlbumId =>
        LayeredNode(
          generator.getElement(a),
          generator.get(a).songs.map { s => LayeredNode(generator.getElement(s)) },
        )
      case s: SongId =>
        LayeredNode(generator.getElement(s))
    }

    val (pagePath, oppositePagePath) =
      SitePage.pageAndOppositePagePath(
        PAGE_PATH,
        musicPage.id,
        musicPage.id.copy(dark = !musicPage.id.dark),
        musicPage.id.dark,
        generator,
      )

    val mainPage = MusicPage(
      music,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Music",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Music",
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(Site.DEFAULT_COVER_IMAGE.source),
        SitePage.canonicalUrlFor(pagePath),
        pagePath.withExtension(Common.HTML_EXTENSION),
        oppositePagePath.map(SitePage.urlFor(_)),
        None,
        musicPage.id.dark,
      ),
      site,
    )

    Seq(mainPage)
  }

}
