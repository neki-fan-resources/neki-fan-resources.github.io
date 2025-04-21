package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.data.Song
import org.skyluc.html._
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.neki_site.html.Compilers
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.LyricsSection
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Path
import org.skyluc.neki_site.html.TitleAndDescription

class SongPage(song: Song, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(compilers.elementDataCompiler.get(song))

    val multimediaBlock = compilers.multimediaDataCompiler.get(song.multimedia, song.linkedTo)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, Song.FROM_KEY)

    val lyricsSection = song.lyrics.map(LyricsSection.generate).getOrElse(Seq())

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, Song.FROM_KEY)

    Seq(
      largeDetails
    ) ++ multiMediaMainSections
      ++ lyricsSection
      ++ additionalSection
  }

}

object SongPage {
  val DESIGNATION = "song"

  val LABEL_LYRICIST = "lyricist"
  val LABEL_COMPOSER = "composer"

  val DARK_PATH = Path("dark")

  def pagesFor(song: Song, compilers: Compilers): Seq[SitePage] = {
    val extraPath = if (song.multimedia.extra(song.linkedTo).isEmpty) {
      None
    } else {
      Some(Path(Common.EXTRA).resolve(song.id.path))
    }

    val compiledData = compilers.elementDataCompiler.get(song)

    val (pagePath, oppositePagePath) =
      SitePage.pageAndOppositePagePath(song.id, song.id.copy(dark = !song.id.dark), song.id.dark, compilers)

    val mainPage = SongPage(
      song,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          song.fullname,
          song.fullnameEn,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          song.fullname,
          song.fullnameEn,
          None,
          None,
        ),
        compiledData.cover.source,
        SitePage.canonicalUrlFor(pagePath),
        pagePath.withExtension(Common.HTML_EXTENSION),
        oppositePagePath.map(SitePage.urlFor(_)),
        extraPath.map(SitePage.urlFor(_)),
        song.id.dark,
      ),
      compilers,
    )

    extraPath
      .map { extraPath =>
        val extraPage = SongExtraPage(
          song,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              song.fullname,
              song.fullnameEn,
              None,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              song.fullname,
              song.fullnameEn,
              None,
              None,
            ),
            compiledData.cover.source,
            SitePage.canonicalUrlFor(extraPath),
            extraPath.withExtension(Common.HTML_EXTENSION),
            None,
            None,
            song.id.dark,
          ),
          compilers,
        )
        Seq(extraPage, mainPage)
      }
      .getOrElse(
        Seq(
          mainPage
        )
      )
  }
}
