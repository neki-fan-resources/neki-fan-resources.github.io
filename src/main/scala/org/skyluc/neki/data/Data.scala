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
    pages: Map[PageId, Page],
    shows: Map[ShowId, Show],
    tours: Map[TourId, Tour],
)

case class DataError(id: Id, errorMessage: String, fatal: Boolean = true) extends SiteError {
  override def toString(): String = s"$id: $errorMessage"
}

object DataBuilder {

  case class TempData(
      site: Option[Site],
      albums: Map[AlbumId, Album],
      songs: Map[SongId, Song],
      pages: Map[PageId, Page],
      shows: Map[ShowId, Show],
      tours: Map[TourId, Tour],
  ) {
    def toData(): Data = {
      // TODO: missing site error support
      Data(site.get, albums, songs, pages, shows, tours)
    }
  }

  def load(elements: List[Item[?]]): Step1 = {
    val data = TempData(None, HashMap(), HashMap(), HashMap(), HashMap(), HashMap())
    val res = elements.foldLeft(new WithErrors(data, Nil)) { (acc, item) =>
      item match {
        case a: Album =>
          acc.copy(t = acc.t.copy(albums = acc.t.albums + ((a.id, a))))
        case p: Page =>
          acc.copy(t = acc.t.copy(pages = acc.t.pages + ((p.id, p))))
        case s: Show =>
          acc.copy(t = acc.t.copy(shows = acc.t.shows + ((s.id, s))))
        case s: Site =>
          // TODO: check if already exists
          acc.copy(t = acc.t.copy(site = Some(s)))
        case s: Song =>
          acc.copy(t = acc.t.copy(songs = acc.t.songs + ((s.id, s))))
        case u =>
          acc.copy(es = acc.es :+ DataError(Site.ID, s"Unsupporting in data loading: $u"))
      }
    }
    new Step1(res.t, res.es)
  }

  class Step1(data: TempData, errors: List[DataError]) {
    // TODO: check cross reference (like song <-> album) ?
    def crossReference() = new Step2(data.toData(), errors)
  }

  class Step2(data: Data, errors: List[DataError]) {
    def checkReferences() = {
      val (songErrors, data2) = checkSongToAlbum(data)
      val (albumErrors, data3) = checkAlbumtoSongs(data2)
      new Step3(data3, errors ::: songErrors ::: albumErrors)
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
      val (songErrors, songs) = checkListCoverImage(data.songs.values, data)
      val (albumErrors, albums) = checkListCoverImage(data.albums.values, data)
      val (showErrors, shows) = checkListCoverImage(data.shows.values, data)

      new Step4(
        Data(
          data.site,
          albums.map(e => (e.id, e)).toMap,
          songs.map(e => (e.id, e)).toMap,
          data.pages,
          shows.map(e => (e.id, e)).toMap,
          data.tours,
        ),
        errors ::: songErrors ::: albumErrors ::: showErrors,
      )
    }

    def checkListCoverImage[T <: WithCoverImage[T]](items: Iterable[T], data: Data): (List[DataError], List[T]) = {
      val res = items.map(checkCoverImage(_, data)).toList
      (res.flatMap(_.e), res.map(_.t))
    }

    def checkCoverImage[T <: WithCoverImage[T]](item: T, data: Data): WithError[T] = {
      item.coverImage match {
        case FileCoverImage(filename, _) =>
          if (Assets.exists(Assets.ASSET_IMAGE_PATH, item.id.path, filename)) {
            WithError(item)
          } else {
            WithError(item, DataError(item.id, s"Cover image file not found: $filename", false))
          }
        case AlbumCoverImage(albumId) =>
          if (data.albums.contains(albumId)) {
            WithError(item)
          } else {
            WithError(item, DataError(item.id, s"Reference not found for cover image: album '$albumId.id'"))
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
    def apply[T <: WithErrorSupport[T]](t: T, e: DataError): WithError[T] =
      WithError(if (e.fatal) t.errored() else t, Some(e))
  }

  case class WithErrors[T](t: T, es: List[DataError])

  object WithErrors {
    // def apply[T](t: T): WithError[T] = WithError(t, Nil)
    def apply[T <: Item[T]](t: T, es: List[DataError]): WithErrors[T] =
      new WithErrors(if (es.exists(_.fatal)) t.errored() else t, es)
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
