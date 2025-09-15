package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Lyrics
import org.skyluc.fan_resources.data.Song
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.LyricsSection
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.*
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class SongPage(
    songCompiledData: ElementCompiledData,
    multimediaBlock: MultiMediaBlockCompiledData,
    lyrics: Option[Lyrics],
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(songCompiledData)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, songCompiledData.uId)

    val lyricsSection = lyrics.map(LyricsSection.generate).getOrElse(Seq())

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, songCompiledData.uId)

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

  def pagesFor(song: Song, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val compiledData = generator.getElement(song)
    val multimediaBlock = generator.getMultiMediaBlock(song)

    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(song.id.path.insertSecond(Common.EXTRA))
    }

    val (pagePath, oppositePagePath) =
      SitePage.pageAndOppositePagePath(song.id, song.id.copy(dark = !song.id.dark), song.id.dark, generator)

    val mainPage = SongPage(
      compiledData,
      multimediaBlock,
      song.lyrics.headOption.map(generator.get(_)),
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
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(pagePath),
        pagePath.withExtension(Common.HTML_EXTENSION),
        oppositePagePath.map(SitePage.urlFor(_)),
        extraPath.map(SitePage.urlFor(_)),
        song.id.dark,
        false,
      ),
      site,
    )

    extraPath
      .map { extraPath =>
        val extraPage = SongExtraPage(
          compiledData,
          multimediaBlock,
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
            SitePage.absoluteUrl(compiledData.cover.source),
            SitePage.canonicalUrlFor(extraPath),
            extraPath.withExtension(Common.HTML_EXTENSION),
            None,
            None,
            song.id.dark,
          ),
          site,
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
