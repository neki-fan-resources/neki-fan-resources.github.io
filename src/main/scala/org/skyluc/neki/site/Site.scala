package org.skyluc.neki.site

import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileVisitResult
import java.io.IOException
import org.skyluc.neki.html.Pages
import org.skyluc.neki.data.Data
import org.skyluc.neki.SiteError

object Site {

  final val STATIC_FILES = Path.of("static")

  def generate(data: Data, errors: List[SiteError], siteFolder: Path): Unit = {
    deleteDirectoryContent(siteFolder)

    // generate pages
    val pages = Pages.fromData(data, errors)
    Pages.generate(pages, siteFolder)

    copyAssets(siteFolder)
  }

  private def copyAssets(siteFolder: Path): Unit = {
    copyDirectoryContent(STATIC_FILES, siteFolder)
  }

  private def copyDirectoryContent(source: Path, destination: Path): Unit = {
    Files.walkFileTree(
      source,
      new SimpleFileVisitor[Path]() {
        override def visitFile(
            file: Path,
            attrs: BasicFileAttributes,
        ): FileVisitResult = {
          Files.copy(file, destination.resolve(source.relativize(file)))
          FileVisitResult.CONTINUE
        }
        override def preVisitDirectory(
            dir: Path,
            attrs: BasicFileAttributes,
        ): FileVisitResult = {
          Files.createDirectories(destination.resolve(source.relativize(dir)))
          return FileVisitResult.CONTINUE
        }
      },
    );
  }

  private def deleteDirectoryContent(directory: Path): Unit = {
    Files.walkFileTree(
      directory,
      new SimpleFileVisitor[Path]() {
        override def visitFile(
            file: Path,
            attrs: BasicFileAttributes,
        ): FileVisitResult = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }
        override def postVisitDirectory(
            dir: Path,
            e: IOException,
        ): FileVisitResult = {
          if (e == null) {
            if (! directory.equals(dir)) {
              Files.delete(dir)
            }
            return FileVisitResult.CONTINUE
          } else {
            throw e
          }
        }
      },
    );
  }
}