package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html._
import Html._
import java.nio.file.Path
import org.skyluc.neki.html.Pages
import org.skyluc.neki.html.MainIntro
import org.skyluc.neki.html.CommonBase

class SourcesPage(data: Data) extends Page(data) {

  import SourcesPage._

  override def path(): Path = Path.of(LIVE_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {

    val pages =
      SourceCategory(
        "Pages",
        List(
          SourceItem(
            "Band",
            List(
              SourceEntry(
                "Band image",
                "frame from 'Get Over' music video",
                Some("https://www.youtube.com/watch?v=TWbLqba3oag"),
              )
            ),
          )
        ),
      )

    val songItems = data.songs.values.toList.sortBy(_.releaseDate).flatMap(_.sources())
    val songs = if (songItems.isEmpty) {
      None
    } else {
      Some(SourceCategory("Songs", songItems))
    }

    val albumItems = data.albums.values.toList.sortBy(_.releaseDate).flatMap(_.sources())
    val albums = if (albumItems.isEmpty) {
      None
    } else {
      Some(SourceCategory("Albums", albumItems))
    }

    val showItems = data.shows.values.toList.sortBy(_.date).flatMap(_.sources())
    val shows = if (showItems.isEmpty) {
      None
    } else {
      Some(SourceCategory("Shows", showItems))
    }

    val tourItems = data.tours.values.toList.sortBy(_.firstDate).flatMap(_.sources())
    val tours = if (tourItems.isEmpty) {
      None
    } else {
      Some(SourceCategory("Tours", tourItems))
    }

    val mediaItems = data.medias.values.toList.sortBy(_.publishedDate).flatMap(_.sources())
    val medias = if (mediaItems.isEmpty) {
      None
    } else {
      Some(SourceCategory("Media", mediaItems))
    }

    val sources = List(Some(pages), songs, albums, shows, tours, medias).flatten

    List(
      MainIntro.generate("The original sources of the data used throughout the website."),
      table()
        .withClass(CLASS_SOURCES_BLOCK)
        .appendTbody(
          tbody().appendTrs(
            sources.flatMap { generateCategory(_) }*
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
                  .withTarget(CommonBase.BLANK)
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

  val LIVE_PATH = "sources" + Pages.HTML_EXTENSION
  val DESIGNATION = "Sources"

  val TEXT_SEPARATOR = "-"

  val CLASS_SOURCES_BLOCK = "sources-block"
  val CLASS_SOURCES_SPACER = "sources-spacer"
  val CLASS_SOURCES_CATEGORY_LABEL = "sources-category-label"
  val CLASS_SOURCES_ITEM_LABEL = "sources-item-label"
  val CLASS_SOURCES_ENTRY = "sources-entry"
  val CLASS_SOURCES_ENTRY_LABEL = "sources-entry-label"

}
