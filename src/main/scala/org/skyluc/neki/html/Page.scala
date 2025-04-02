package org.skyluc.neki.html

import org.skyluc.html.Html
import java.nio.file.Path
import org.skyluc.neki.data.{MusicPage => dMusicPage, Page => dPage, ShowsPage => dShowsPage, Data}
import org.skyluc.html.HtmlRenderer
import java.nio.file.Files
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileVisitResult
import java.io.IOException
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

// TODO: remove the data field ??
abstract class Page(val data: Data) {

  def path(): Path

  val isRoot: Boolean = false

  def shortTitle(): String

  def altName(): Option[String]

  def pageContent(): Html = CommonBase.generate(this)

  def mainContent(): List[BodyElement[?]]

}

object Pages {

  val HTML_EXTENSION = ".html"
  val HTML_SEPARATOR = "/"
  val TEXT_PLACEHOLDER = "&nbsp;"

  def fromData(data: Data, errors: List[SiteError]): Iterable[Page] = {
    Iterable(ErrorPage(errors, data), LivePage(data))
      ++ { if (data.pages.music.error) None else Some(MusicPage(data.pages.music, data)) }
      ++ { if (data.pages.shows.error) None else Some(ShowsPage(data.pages.shows, data)) }
      ++ data.albums.values.filterNot(_.error).map(AlbumPage(_, data))
      ++ data.songs.values.filterNot(_.error).map(SongPage(_, data))
      ++ data.shows.values.filterNot(_.error).map(ShowPage(_, data))
      ++ data.tours.values.filterNot(_.error).map(TourPage(_, data))
  }

  def generate(pages: Iterable[Page], siteFolder: Path): Unit = {
    pages.foreach { page =>
      val renderer = new HtmlRenderer()
      renderer.visit(page.pageContent())
      val path = siteFolder.resolve(page.path())
      Files.createDirectories(path.getParent())
      val output = Files.newBufferedWriter(
        siteFolder.resolve(page.path()).toAbsolutePath()
      )
      output.write(renderer.result)
      output.close()
    }
  }

}
