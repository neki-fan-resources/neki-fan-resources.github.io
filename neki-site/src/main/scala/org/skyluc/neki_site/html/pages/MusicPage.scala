package org.skyluc.neki_site.html.pages

import org.skyluc.neki_site.{data => d}
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html.BodyElement
import org.skyluc.fan_resources.Common
import org.skyluc.neki_site.html.component.Defaults
import org.skyluc.fan_resources.data._
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.collection.LayeredData
import org.skyluc.collection.LayeredNode
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.neki_site.html.TitleAndDescription

class MusicPage(musicPage: d.MusicPage, pageDescription: PageDescription, compilers: Compilers)
    extends SitePage(pageDescription, compilers) {

  import MusicPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val tree: LayeredData[ElementCompiledData] = musicPage.music.map {
      case a: AlbumId =>
        LayeredNode(
          compilers.elementDataCompiler.get(a),
          compilers.data.get(a).songs.map { s => LayeredNode(compilers.elementDataCompiler.get(s)) },
        )
      case s: SongId =>
        LayeredNode(compilers.elementDataCompiler.get(s))
    }

    List(
      MainIntro.generate(MAIN_INTRO_TEXT),
      MediumCard.generateTree(tree),
    )
  }

}

object MusicPage {

  val MAIN_INTRO_TEXT = "The albums and songs released by NEK!."

  val PAGE_PATH = Path("music")

  def pagesFor(musicPage: d.MusicPage, compilers: Compilers): Seq[SitePage] = {
    val mainPage = MusicPage(
      musicPage,
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
