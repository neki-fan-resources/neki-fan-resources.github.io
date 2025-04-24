package org.skyluc.neki_site.html.pages

import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.*
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class MusicPage(music: LayeredData[ElementCompiledData], pageDescription: PageDescription, site: d.Site)
    extends SitePage(pageDescription, site) {

  import MusicPage._

  override def elementContent(): Seq[BodyElement[?]] = {

    List(
      MainIntro.generate(MAIN_INTRO_TEXT),
      MediumCard.generateTree(music),
    )
  }

}

object MusicPage {

  val MAIN_INTRO_TEXT = "The albums and songs released by NEK!."

  val PAGE_PATH = Path("music")

  def pagesFor(musicPage: d.MusicPage, compilers: Compilers): Seq[SitePage] = {

    val music: LayeredData[ElementCompiledData] = musicPage.music.map {
      case a: AlbumId =>
        LayeredNode(
          compilers.elementDataCompiler.get(a),
          compilers.data.get(a).songs.map { s => LayeredNode(compilers.elementDataCompiler.get(s)) },
        )
      case s: SongId =>
        LayeredNode(compilers.elementDataCompiler.get(s))
    }

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
        SitePage.canonicalUrlFor(PAGE_PATH),
        PAGE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      compilers.data.site,
    )

    Seq(mainPage)
  }

}
