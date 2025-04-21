package org.skyluc.neki_site.html.pages

import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.html._
import Html._
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.Common
import org.skyluc.neki_site.html.component.Defaults
import org.skyluc.fan_resources.data.Path
import org.skyluc.neki_site.html.SourcesExtractor
import org.skyluc.neki_site.html.TitleAndDescription

class SourcesPage(data: Seq[SourcesPage.SourceCategory], description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  import SourcesPage._

  override def elementContent(): Seq[BodyElement[?]] = {
    List(
      MainIntro.generate("The original sources of the data used throughout the website."),
      table()
        .withClass(CLASS_SOURCES_BLOCK)
        .appendTbody(
          tbody().appendTrs(
            data.flatMap { generateCategory(_) }*
          )
        ),
    )

  }

  private def generateCategory(category: SourceCategory): List[Tr] = {
    tr().appendTds(
      td()
        .withClass(CLASS_SOURCES_CATEGORY_LABEL)
        .withSpan(4)
        .appendElements(text(category.label))
    )
      :: category.items.flatMap(generateItem(_))
  }

  private def generateItem(item: SourceItem): List[Tr] = {
    tr().appendTds(
      td().withClass(CLASS_SOURCES_SPACER),
      td().withClass(CLASS_SOURCES_ITEM_LABEL).withSpan(3).appendElements(text(item.label)),
    ) :: item.entries.map(generateEntry(_))
  }

  private def generateEntry(entry: SourceEntry): Tr = {
    tr()
      .withClass(CLASS_SOURCES_ENTRY)
      .appendTds(
        td().withClass(CLASS_SOURCES_SPACER),
        td().withClass(CLASS_SOURCES_ENTRY_LABEL).appendElements(text(entry.label)),
        td().appendElements(text(TEXT_SEPARATOR)),
        td()
          .appendElements(
            entry.url
              .map { url =>
                a()
                  .withHref(url)
                  .withTarget(Common.BLANK)
                  .appendElements(
                    text(entry.description)
                  )
              }
              .getOrElse(text(entry.description))
          ),
      )
  }
}

object SourcesPage {

  val PAGE_PATH = Path("sources")

  val TEXT_SEPARATOR = "-"

  // classes
  val CLASS_SOURCES_BLOCK = "sources-block"
  val CLASS_SOURCES_SPACER = "sources-spacer"
  val CLASS_SOURCES_CATEGORY_LABEL = "sources-category-label"
  val CLASS_SOURCES_ITEM_LABEL = "sources-item-label"
  val CLASS_SOURCES_ENTRY = "sources-entry"
  val CLASS_SOURCES_ENTRY_LABEL = "sources-entry-label"

  case class SourceCategory(
      label: String,
      items: List[SourceItem],
  )

  case class SourceItem(
      label: String,
      entries: List[SourceEntry],
  )

  case class SourceEntry(
      label: String,
      description: String,
      url: Option[String],
  )

  def pages(compilers: Compilers): Seq[SitePage] = {

    val data = SourcesExtractor.getAll(compilers)

    val mainPage = SourcesPage(
      data,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Sources",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Sources",
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
