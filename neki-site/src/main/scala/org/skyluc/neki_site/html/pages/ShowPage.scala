package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.*
import org.skyluc.fan_resources.html.component.LargeDetails
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription

class ShowPage(show: Show, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  override def elementContent(): Seq[BodyElement[?]] = {
    val largeDetails =
      LargeDetails.generate(compilers.elementDataCompiler.get(show))

    val multimediaBlock = compilers.multimediaDataCompiler.get(show.multimedia, show.linkedTo)

    val multiMediaMainSections = MultiMediaCard.generateMainSections(multimediaBlock, Show.FROM_KEY)

    val additionalSection = MultiMediaCard.generateAdditionalSection(multimediaBlock, Show.FROM_KEY)

    Seq(
      largeDetails
    ) ++ multiMediaMainSections
      ++ additionalSection
  }
}

object ShowPage {
  val DESIGNATION = "show"

  val URL_SETLISTFM_BASE = "https://www.setlist.fm/setlist/"

  val LABEL_VENUE = "venue"
  val LABEL_SETLIST = "setlist"
  val VALUE_SETLIST = "setlist.fm"
  val VALUE_EVENT_PAGE = "event page"

  def pagesFor(show: Show, compilers: Compilers): Seq[SitePage] = {
    val extraPath = if (show.multimedia.extra(show.linkedTo).isEmpty) {
      None
    } else {
      Some(Path(Common.EXTRA).resolve(show.id.path))
    }

    val compiledData = compilers.elementDataCompiler.get(show)

    val mainPage = ShowPage(
      show,
      PageDescription(
        TitleAndDescription.formattedTitle(
          Some(compiledData.designation),
          None,
          show.fullname,
          None,
          show.shortname,
          None,
        ),
        TitleAndDescription.formattedDescription(
          Some(compiledData.designation),
          None,
          show.fullname,
          None,
          show.shortname,
          None,
        ),
        SitePage.absoluteUrl(compiledData.cover.source),
        SitePage.canonicalUrlFor(show.id.path),
        show.id.path.withExtension(Common.HTML_EXTENSION),
        None,
        extraPath.map(SitePage.urlFor(_)),
        false,
      ),
      compilers,
    )

    extraPath
      .map { extraPath =>
        val extraPage = ShowExtraPage(
          show,
          PageDescription(
            TitleAndDescription.formattedTitle(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              show.fullname,
              None,
              show.shortname,
              None,
            ),
            TitleAndDescription.formattedDescription(
              Some(compiledData.designation),
              TitleAndDescription.EXTRA,
              show.fullname,
              None,
              show.shortname,
              None,
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
      .getOrElse(
        Seq(
          mainPage
        )
      )
  }
}
