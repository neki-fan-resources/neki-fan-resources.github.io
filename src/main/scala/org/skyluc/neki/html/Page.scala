package org.skyluc.neki.html

import org.skyluc.html.Html
import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.html.HtmlRenderer
import java.nio.file.Files
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileVisitResult
import java.io.IOException
import org.skyluc.neki.yaml.ParserError
import page.AlbumPage
import page.SongPage
import page.ErrorPage

abstract class Page(val data: Data) {

  def path(): Path

  def content(): Html

  val isRoot: Boolean = false

  def shortTitle(): String

  def altName(): Option[String]
}

object Pages {

  final val HTML_EXTENSION = ".html"

  def fromData(data: Data, errors: List[ParserError]): Iterable[Page] = {
    Iterable(ErrorPage(errors, data)) ++ data.albums.values.map(AlbumPage(_, data)) ++ data.songs.values.map(SongPage(_, data))
  }

  def generate(pages: Iterable[Page], siteFolder: Path): Unit = {
    pages.foreach { page =>
      val renderer = new HtmlRenderer()
      renderer.visit(page.content())
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
