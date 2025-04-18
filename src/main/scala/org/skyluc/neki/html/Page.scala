package org.skyluc.neki.html

import org.skyluc.html.Html
import java.nio.file.Path
import org.skyluc.neki.data.{Page => dPage, Data}
import org.skyluc.html.HtmlRenderer
import java.nio.file.Files
import page.AlbumPage
import page.SongPage
import page.ErrorPage
import page.MusicPage
import org.skyluc.html.BodyElement
import org.skyluc.neki.SiteError
import org.skyluc.neki.html.page.ShowsPage
import org.skyluc.neki.html.page.ShowPage
import org.skyluc.neki.html.page.TourPage
import org.skyluc.neki.html.page.LivePage
import org.skyluc.neki.html.page.BandPage
import org.skyluc.neki.html.page.ChronologyPage
import org.skyluc.neki.html.page.MediasPage
import org.skyluc.neki.html.page.MediaPage
import org.skyluc.neki.html.page.AboutPage
import org.skyluc.neki.html.page.SourcesPage

// TODO: remove the data field ??
abstract class Page(val data: Data) {

  def path(): Path

  def url(): Path = path()

  val isRoot: Boolean = false
  val isDark: Boolean = false
  def oppositePage: Option[String] = None

  def shortTitle(): String

  def altName(): Option[String]

  def ogImageUrl(): Option[String] = None

  def pageContent(): Html = CommonBase.generate(this)

  def mainContent(): List[BodyElement[?]]

}

object Pages {

  val HTML_EXTENSION = ".html"
  val HTML_SEPARATOR = "/"
  val TEXT_PLACEHOLDER = "&nbsp;"

  val EXTRA_PATH = "extra"

  def fromData(data: Data, errors: List[SiteError]): Iterable[Page] = {
    Iterable(
      ErrorPage(errors, data),
      BandPage(data),
      LivePage(data),
      MediasPage(data),
      AboutPage(data),
      SourcesPage(data),
    )
      ++ data.pages.values.filterNot(_.error).map(pageFor(_, data))
      ++ data.albums.values.filterNot(_.error).flatMap(AlbumPage.pagesFor(_, data))
      ++ data.songs.values.filterNot(_.error).flatMap(SongPage.pagesFor(_, data))
      ++ data.shows.values.filterNot(_.error).flatMap(ShowPage.pagesFor(_, data))
      ++ data.tours.values.filterNot(_.error).map(TourPage(_, data))
      ++ data.medias.values.filterNot(_.error).map(MediaPage(_, data))
  }

  def pageFor(page: dPage, data: Data): Page = {
    page match {
      case c: org.skyluc.neki.data.ChronologyPage =>
        ChronologyPage(c, data)
      case m: org.skyluc.neki.data.MusicPage =>
        MusicPage(m, data)
      case s: org.skyluc.neki.data.ShowsPage =>
        ShowsPage(s, data)
    }
  }

  def generate(pages: Iterable[Page], siteFolder: Path): Unit = {
    pages.foreach { page =>
      val renderer = new HtmlRenderer()
      renderer.visit(page.pageContent())
      val path = siteFolder.resolve(page.path())
      Files.createDirectories(path.getParent())
      val output = Files.newBufferedWriter(path.toAbsolutePath())
      output.write(renderer.result)
      output.close()
    }
  }

}
