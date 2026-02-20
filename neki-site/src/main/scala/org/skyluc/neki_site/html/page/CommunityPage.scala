package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*

import d.Path

class CommunityPage(sections: Seq[fr.component.ContentSection]) extends MainSitePage {

  val CLASS_ABOUT_SECTION = "about-section"

  override val outputPath: Path = Path("community.html")

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle("Community"),
      TitleGenerator.generateTitle("Community"),
      outputPath.toAbsoluteString(),
      MainSitePage.imageLogo.imageUrl,
    )

  override def mainContent(): Seq[BodyElement[?]] = {

    sections.flatMap(_.content())
  }
}

object CommunityPage {

  def pagesFor(generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.MainSitePage] = {

    val communityId = d.EventId("community")

    val content = d.BasicDisplaySection(
      Some("Community"),
      Seq(
        d.TextDisplaySubsection(
          None,
          None,
          false,
          Seq(
            d.Line(
              Seq(
                d.LineSectionText(
                  "Projects, creators and other resources which are part of the community of NEK!'s fans."
                )
              )
            )
          ),
        ),
        d.TextDisplaySubsection(
          Some("NEK! Fan Resources YouTube channel"),
          Some(d.LocalImageId(communityId, "nfr_youtube")),
          true,
          Seq(
            d.Line(
              Seq(
                d.LineSectionText("The "),
                d.LineSectionWebItem(d.WebPageId("youtube.com", "lucbourlier"), None),
                d.LineSectionText(
                  " for this site. It contains all the subtitled videos displayed on the site."
                ),
              )
            )
          ),
        ),
        d.TextDisplaySubsection(
          Some("SkyLuc YouTube channel"),
          Some(d.LocalImageId(communityId, "skyluc_youtube")),
          true,
          Seq(
            d.Line(
              Seq(
                d.LineSectionWebItem(d.WebPageId("youtube.com", "lucbourlier"), None),
                d.LineSectionText(
                  " of a fan, with videos of footage compilation for multiple NEK! songs, and some other NEK! related videos."
                ),
              )
            )
          ),
        ),
      ),
    )

    val element = generator.get(communityId)

    val subsectionGenerator =
      fr.compileddata.ContentCompiledDataGenerator.SubsectionGenerator(element, element.id.path, generator)
    val sectionGenerator = fr.compileddata.ContentCompiledDataGenerator.SectionGenerator(
      element,
      element.id.path,
      subsectionGenerator,
      generator,
    )

    val renderedContent = content.process(sectionGenerator)

    Seq(CommunityPage(renderedContent.toSeq))
  }

}
