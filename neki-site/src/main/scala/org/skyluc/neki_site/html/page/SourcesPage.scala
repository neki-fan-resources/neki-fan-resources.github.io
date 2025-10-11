package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Data
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.html.*
import org.skyluc.neki_site.html.*

import Html.*

class SourcesPage(data: Seq[SourcesPage.SourceCategory]) extends MainSitePage {

  import SourcesPage.*

  override val outputPath: Path = Path("sources.html")

  override val pageConfiguration: fr.page.MainSitePageConfiguration =
    MainSitePageConfiguration(
      TitleGenerator.generateTitle("Sources"),
      TitleGenerator.generateDescription("Sources"),
      outputPath.toAbsoluteString(),
      MainSitePage.imageLogo.imageUrl,
    )

  override def mainContent(): Seq[BodyElement[?]] = {
    List(
      fr.component.MainIntro.generate(Seq("The original sources of the data used throughout the website.")),
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
        td().appendElements(text(Common.SEPARATOR)),
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

  private val CLASS_SOURCES_BLOCK = "sources-block"
  private val CLASS_SOURCES_SPACER = "sources-spacer"
  private val CLASS_SOURCES_CATEGORY_LABEL = "sources-category-label"
  private val CLASS_SOURCES_ITEM_LABEL = "sources-item-label"
  private val CLASS_SOURCES_ENTRY = "sources-entry"
  private val CLASS_SOURCES_ENTRY_LABEL = "sources-entry-label"

  def pagesFor(data: Data, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.MainSitePage] = {

    val sources = compileddata.SourcesExtractor.getAll(data.elements.view.values.toSeq, generator)
    Seq(
      SourcesPage(sources)
    )
  }

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
}
