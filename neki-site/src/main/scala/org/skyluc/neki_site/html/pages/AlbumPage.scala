package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Album
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.fan_resources.html.CompiledDataGenerator

class AlbumPage(
    album: ElementCompiledData,
    songs: Seq[ElementCompiledData],
    multimediaBlock: MultiMediaBlockCompiledData,
    description: PageDescription,
    site: Site,
) extends SitePage(description, site) {

  import AlbumPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(album)

    val songsSection: Seq[BodyElement[?]] = Seq(
      SectionHeader.generate(SECTION_SONGS),
      MediumCard.generateList(
        songs
      ),
    )

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, Album.FROM_KEY)

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, Album.FROM_KEY)

    Seq(
      largeDetails
    ) ++ songsSection
      ++ multiMediaMainSections
      ++ additionalSection

  }
}

object AlbumPage {

  val SECTION_SONGS = "Songs"

  def pagesFor(album: Album, site: Site, generator: CompiledDataGenerator): Seq[SitePage] = {

    val compiledData = generator.getElement(album)

    val songs = album.songs.map(generator.getElement)

    val multimediaBlock = generator.getMultiMediaBlock(album)

    // TODO: used the information from the multimedia block compiledData (need to be cached)
    val extraPath = if (multimediaBlock.extra.isEmpty) {
      None
    } else {
      Some(album.id.path.insertSecond(Common.EXTRA))
    }

    // No dark page support for albums yet
    // val (pagePath, oppositePagePath) =
    //   SitePage.pageAndOppositePagePath(album.id, album.id.copy(dark = !album.id.dark), album.id.dark, compilers)

    val mainPage = AlbumPage(
      compiledData,
      songs,
      multimediaBlock,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          album.fullname,
          None,
          None,
          album.altname,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          album.fullname,
          None,
          None,
          album.altname,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(album.id.path),
        album.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
      ),
      site,
    )

    extraPath
      .map { extraPath =>
        val extraPage = AlbumExtraPage(
          compiledData,
          multimediaBlock,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              album.fullname,
              None,
              None,
              album.altname,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              album.fullname,
              None,
              None,
              album.altname,
            ),
            SitePage.absoluteUrl(compiledData.cover.source),
            SitePage.canonicalUrlFor(extraPath),
            extraPath.withExtension(Common.HTML_EXTENSION),
            None,
            None,
            false,
          ),
          site,
        )
        Seq(extraPage, mainPage)
      }
      .getOrElse(Seq(mainPage))

  }
}
