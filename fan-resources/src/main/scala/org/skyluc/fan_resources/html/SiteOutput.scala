package org.skyluc.fan_resources.html

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

object SiteOutput {

  def generate(pages: Iterable[Page], staticFolder: Path, siteFolder: Path): Unit = {
    deleteDirectoryContent(siteFolder)

    copyDirectoryContent(staticFolder, siteFolder)

    pages.foreach { page =>
      val path = siteFolder.resolve(page.outputPath.asFilePath())
      Files.createDirectories(path.getParent())
      val output = Files.newBufferedWriter(path.toAbsolutePath())
      output.write(page.content())
      output.close()
    }
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
    if (Files.isDirectory(directory)) {
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
              if (!directory.equals(dir)) {
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

}
