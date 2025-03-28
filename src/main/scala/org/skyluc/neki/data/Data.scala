package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.Main.DATA_PATH
import java.nio.file.Paths
import java.nio.file.Files

import scala.collection.JavaConverters._
import org.skyluc.neki.yaml.Element
import scala.collection.immutable.HashMap

case class Data (
  albums: Map[AlbumId, Album],
  songs: Map[SongId, Song]
)

object DataBuilder {
  class Step1(data: Data) {
    def done = data
  }

  def load(elements: List[Item]): Step1 = {
    val data = Data(HashMap(), HashMap())
    val res = elements.foldLeft(data){ (acc, item) =>
      item match {
        case a: Album =>
          acc.copy(albums = acc.albums + ((a.id, a)))
        case s: Song =>
          acc.copy(songs = acc.songs + ((s.id, s)))
      }
    }
    new Step1(res)
  }
}

object Data {

  def listAllFiles(dataFolder: Path): List[Path] = {

    listAllYamlFiles(dataFolder :: Nil, Nil)
  }

  private def listAllYamlFiles(paths: List[Path], acc: List[Path]): List[Path] = {
    paths match {
      case head :: tail => 
        val folders: List[Path] = Files.list(head).filter(a => Files.isDirectory(a)).toList().asScala.toList
        val files: List[Path] = Files.list(head).filter(a => Files.isRegularFile(a) && a.getFileName().toString().endsWith(".yml")).toList().asScala.toList
        listAllYamlFiles(folders ::: tail, acc ::: files)
      case Nil => acc
    }
  }

}