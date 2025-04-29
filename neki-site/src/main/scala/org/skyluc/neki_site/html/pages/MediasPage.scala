package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Media
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.html.component.MediumCard
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.data.Site as dSite
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Site
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.data.Datum

class MediasPage(medias: Seq[ElementCompiledData], description: PageDescription, site: dSite)
    extends SitePage(description, site) {

  import MediasPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val mainIntro = MainIntro.generate(MAIN_INTRO_CONTENT)

    val compiledDataList = medias.sortBy(_.date).reverse

    val mediaList = MediumCard.generateList(compiledDataList)

    Seq(mainIntro, mediaList)
  }
}

object MediasPage {

  val MAIN_INTRO_CONTENT = "The media and press events NEK! was part of."

  val PAGE_PATH = Path("medias")

  def pages(datums: Seq[Datum[?]], site: dSite, generator: CompiledDataGenerator): Seq[SitePage] = {
    val medias: Seq[ElementCompiledData] = datums.flatMap {
      case m: Media =>
        Some(generator.getElement(m))
      case _ =>
        None
    }.toSeq

    val mainPage = MediasPage(
      medias,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Medias",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Medias",
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
      site,
    )

    Seq(mainPage)
  }

}
