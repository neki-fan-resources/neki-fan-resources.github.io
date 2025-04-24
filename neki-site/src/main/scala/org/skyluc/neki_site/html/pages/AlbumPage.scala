package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Album
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class AlbumPage(album: Album, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  import AlbumPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(compilers.elementDataCompiler.get(album))

    val songsSection: Seq[BodyElement[?]] = Seq(
      SectionHeader.generate(SECTION_SONGS),
      MediumCard.generateList(
        album.songs.map(compilers.elementDataCompiler.get(_))
      ),
    )

    val multimediaBlock = compilers.multimediaDataCompiler.getBlock(album)

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

  def pagesFor(album: Album, compilers: Compilers): Seq[SitePage] = {

    // TODO: used the information from the multimedia block compiledData (need to be cached)
    val extraPath = if (album.multimedia.extra(album.linkedTo, compilers.data).isEmpty) {
      None
    } else {
      Some(album.id.path.insertSecond(Common.EXTRA))
    }

    val compiledData = compilers.elementDataCompiler.get(album)

    // No dark page support for albums yet
    // val (pagePath, oppositePagePath) =
    //   SitePage.pageAndOppositePagePath(album.id, album.id.copy(dark = !album.id.dark), album.id.dark, compilers)

    val mainPage = AlbumPage(
      album,
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
      compilers,
    )

    extraPath
      .map { extraPath =>
        val extraPage = AlbumExtraPage(
          album,
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
          compilers,
        )
        Seq(extraPage, mainPage)
      }
      .getOrElse(Seq(mainPage))

  }
}
