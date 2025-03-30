package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.SiteError
import java.nio.file.Paths
import java.nio.file.Files

import scala.jdk.CollectionConverters._
import org.skyluc.neki.yaml.Element
import scala.collection.immutable.HashMap

case class Data(
    site: Site,
    albums: Map[AlbumId, Album],
    songs: Map[SongId, Song],
)

case class DataError(id: Id, errorMessage: String) extends SiteError {
  override def toString(): String = s"$id: $errorMessage"
}

object DataBuilder {

  case class TempData(
      site: Option[Site],
      albums: Map[AlbumId, Album],
      songs: Map[SongId, Song],
  ) {
    def toData(): Data = {
      // TODO: error support
      Data(site.get, albums, songs)
    }
  }

  def load(elements: List[Item[?]]): Step1 = {
    val data = TempData(None, HashMap(), HashMap())
    val res = elements.foldLeft(data) { (acc, item) =>
      item match {
        case a: Album =>
          acc.copy(albums = acc.albums + ((a.id, a)))
        case s: Site =>
          acc.copy(site = Some(s))
        case s: Song =>
          acc.copy(songs = acc.songs + ((s.id, s)))
      }
    }
    new Step1(res)
  }

  class Step1(data: TempData) {
    // TODO: check cross reference (like song <-> album) ?
    def crossReference() = new Step2(data.toData())
  }

  class Step2(data: Data) {
    def checkReferences() = {
      val (songErrors, data2) = checkSongToAlbum(data)
      val (albumErrors, data3) = checkAlbumtoSongs(data2)
      println("checkreferences done")
      new Step3(data3, songErrors ::: albumErrors)
    }

    def checkSongToAlbum(data: Data): (List[DataError], Data) = {
      val res = data.songs.values.map(checkSongToAlbum(_, data)).toList
      (res.flatMap(_.e), data.copy(songs = res.map(r => (r.t.id, r.t)).toMap))
    }

    def checkSongToAlbum(song: Song, data: Data): WithError[Song] = {
      song.album
        .map { albumId =>
          if (data.albums.contains(albumId)) {
            WithError(song)
          } else {
            WithError(song, DataError(song.id, s"Referenced album '${albumId.id}' is not found"))
          }
        }
        .getOrElse(WithError(song))
    }

    def checkAlbumtoSongs(data: Data): (List[DataError], Data) = {
      val res = data.albums.values.map(checkAlbumtoSongs(_, data)).toList
      (res.flatMap(_.es), data.copy(albums = res.map(r => (r.t.id, r.t)).toMap))
    }

    def checkAlbumtoSongs(album: Album, data: Data): WithErrors[Album] = {
      val errors = album.songs.flatMap { songId =>
        if (data.songs.contains(songId)) {
          None
        } else {
          Some(DataError(album.id, s"Referenced song '${songId.id}' is not found"))
        }
      }
      WithErrors(album, errors)
    }
  }

  class Step3(data: Data, errors: List[DataError]) {
    def checkAssets() = {
      val (songErrors, songs) = checkListFileCoverImage(data.songs.values)
      val (albumErrors, albums) = checkListFileCoverImage(data.albums.values)

      new Step4(
        Data(data.site, albums.map(e => (e.id, e)).toMap, songs.map(e => (e.id, e)).toMap),
        errors ::: songErrors ::: albumErrors,
      )
    }

    def checkListFileCoverImage[T <: WithCoverImage[T]](items: Iterable[T]): (List[DataError], List[T]) = {
      val res = items.map(checkFileCoverImage(_)).toList
      (res.flatMap(_.e), res.map(_.t))
    }

    def checkFileCoverImage[T <: WithCoverImage[T]](item: T): WithError[T] = {
      item.coverImage match {
        case FileCoverImage(filename, _) =>
          if (Assets.exists(Assets.ASSET_IMAGE_PATH, item.id.path, filename)) {
            WithError(item, None)
          } else {
            WithError(item.errored(), Some(DataError(item.id, s"Cover image file not found: $filename")))
          }
      }
    }

  }

  class Step4(data: Data, errors: List[DataError]) {
    def done = (errors, data)
  }

  case class WithError[T](t: T, e: Option[DataError])

  object WithError {
    def apply[T](t: T): WithError[T] = WithError(t, None)
    def apply[T <: Item[T]](t: T, e: DataError): WithError[T] = WithError(t.errored(), Some(e))
  }

  case class WithErrors[T](t: T, es: List[DataError])

  object WithErrors {
    // def apply[T](t: T): WithError[T] = WithError(t, Nil)
    def apply[T <: Item[T]](t: T, es: List[DataError]): WithErrors[T] =
      new WithErrors(if (es.isEmpty) t else t.errored(), es)
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
        val files: List[Path] = Files
          .list(head)
          .filter(a => Files.isRegularFile(a) && a.getFileName().toString().endsWith(".yml"))
          .toList()
          .asScala
          .toList
        listAllYamlFiles(folders ::: tail, acc ::: files)
      case Nil => acc
    }
  }
}

object Assets {

  def exists(assetPath: Path, itemPath: Path, filename: String): Boolean = {
    Files.exists(assetPath.resolve(itemPath).resolve(filename))
  }

  val ASSET_PATH = Path.of("static", "asset")
  val ASSET_IMAGE_PATH = ASSET_PATH.resolve("image")
}
