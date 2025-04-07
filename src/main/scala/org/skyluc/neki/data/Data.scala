package org.skyluc.neki.data

import java.nio.file.Path
import org.skyluc.neki.SiteError
import java.nio.file.Files

import scala.jdk.CollectionConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable.TreeSet

case class Data(
    site: Site,
    albums: Map[Id[Album], Album],
    songs: Map[Id[Song], Song],
    shows: Map[Id[Show], Show],
    tours: Map[Id[Tour], Tour],
    medias: Map[Id[Media], Media],
    pages: Map[Id[Page], Page],
    multimedia: Map[Id[MultiMedia], MultiMedia],
)

object Data {
  case class Pages(
      music: MusicPage,
      shows: ShowsPage,
  ) {
    def contains(id: Id[?]): Boolean = {
      music.id == id || shows.id == id
    }
  }
}

case class DataError(id: Id[?], errorMessage: String, fatal: Boolean = true) extends SiteError {
  override def toString(): String = s"$id: $errorMessage"
}

object DataBuilder {

  case class TempData(
      site: Option[Site],
      albums: Map[Id[Album], Album],
      songs: Map[Id[Song], Song],
      shows: Map[Id[Show], Show],
      tours: Map[Id[Tour], Tour],
      medias: Map[Id[Media], Media],
      pages: Map[Id[Page], Page],
      multimedia: Map[Id[MultiMedia], MultiMedia],
  ) {
    def toData(): Data = {
      // TODO: missing site error support
      Data(site.get, albums, songs, shows, tours, medias, pages, multimedia)
    }
  }

  def load(elements: List[Item[?]]): Step1 = {
    val data =
      TempData(None, HashMap(), HashMap(), HashMap(), HashMap(), HashMap(), HashMap(), HashMap())
    // TODO: check if adding items which already existing id
    val res = elements.foldLeft(new WithErrors(data, Nil)) { (acc, item) =>
      item match {
        case a: Album =>
          acc.copy(t = acc.t.copy(albums = acc.t.albums + ((a.id, a))))
        case c: ChronologyPage =>
          acc.copy(t = acc.t.copy(pages = acc.t.pages + ((c.id, c))))
        case m: Media =>
          acc.copy(t = acc.t.copy(medias = acc.t.medias + ((m.id, m))))
        case m: MultiMedia =>
          acc.copy(t = acc.t.copy(multimedia = acc.t.multimedia + ((m.id, m))))
        case m: MusicPage =>
          acc.copy(t = acc.t.copy(pages = acc.t.pages + ((m.id, m))))
        case s: Show =>
          acc.copy(t = acc.t.copy(shows = acc.t.shows + ((s.id, s))))
        case s: ShowsPage =>
          acc.copy(t = acc.t.copy(pages = acc.t.pages + ((s.id, s))))
        case s: Site =>
          // TODO: check if already exists
          acc.copy(t = acc.t.copy(site = Some(s)))
        case s: Song =>
          // if (s.id.id == "dreams") println(s)
          acc.copy(t = acc.t.copy(songs = acc.t.songs + ((s.id, s))))
        case t: Tour =>
          acc.copy(t = acc.t.copy(tours = acc.t.tours + ((t.id, t))))
        case u =>
          acc.copy(es = acc.es :+ DataError(Site.ID, s"Unsupporting in data loading: $u"))
      }
    }
    new Step1(res.t, res.es)
  }

  class Step1(data: TempData, errors: List[DataError]) {
    // TODO: check cross reference (like song <-> album, tour <-> show) ?
    def crossReference() = new Step2(data.toData(), errors)
  }

  class Step2(data: Data, errors: List[DataError]) {
    def checkReferences() = {
      val (songErrors, data2) = checkSongToAlbum(data)
      val (albumErrors, data3) = checkAlbumToSongs(data2)
      val (tourErrors, data4) = checkTourToShows(data3)
      val (showErrors, data5) = checkShowToTour(data4)
      val (pageErrors, dataLast) = checkPageReferences(data5)

      // TODO: check relatedTo references
      // TODO: check multimedia references
      // TODO: check reference to multimedia in chronology

      new Step3(dataLast, errors ::: songErrors ::: albumErrors ::: showErrors ::: tourErrors ::: pageErrors)
    }

    def checkSongToAlbum(data: Data): (List[DataError], Data) = {
      val res = data.songs.values.map { song =>
        checkAreKnown(song, song.album, data)
      }
      gatherResult(res, data)((m, data) => data.copy(songs = m))
    }

    def checkAlbumToSongs(data: Data): (List[DataError], Data) = {
      val res = data.albums.values.map { album =>
        checkAreKnown(album, album.songs, data)
      }
      gatherResult(res, data)((m, data) => data.copy(albums = m))
    }

    def checkTourToShows(data: Data): (List[DataError], Data) = {
      val res = data.tours.values.map { tour =>
        checkAreKnown(tour, tour.shows, data)
      }
      gatherResult(res, data)((m, data) => data.copy(tours = m))
    }

    def checkShowToTour(data: Data): (List[DataError], Data) = {
      val res = data.shows.values.map { show =>
        checkAreKnown(show, show.tour, data)
      }
      gatherResult(res, data)((m, data) => data.copy(shows = m))
    }

    def checkPageReferences(data: Data): (List[DataError], Data) = {
      val res = data.pages.values.map {
        case c: ChronologyPage =>
          checkAreKnown(c, c.chronology.referencedIds(), data)
        case m: MusicPage =>
          checkAreKnown(m, m.music, data)
        case s: ShowsPage =>
          checkAreKnown(s, s.shows, data)
      }

      gatherResult(res, data)((m, data) => data.copy(pages = m))
    }

    private def checkAreKnown[T <: Item[T]](item: T, list: Iterable[Id[?]], data: Data): WithErrors[T] = {
      val errors = list.map(_.isKnown(item.id, data)).flatten
      WithErrors(item, errors.toList)
    }

    private def gatherResult[T <: Item[T]](list: Iterable[WithErrors[T]], data: Data)(
        f: (Map[Id[T], T], Data) => Data
    ): (List[DataError], Data) = {
      val errors = list.flatMap(_.es).toList
      val map = list.map(r => (r.t.id, r.t)).toMap
      (errors, f(map, data))
    }

  }

  class Step3(data: Data, errors: List[DataError]) {
    def checkAssets() = {
      val (songErrors, songs) = checkListCoverImage(data.songs.values, data)
      val (albumErrors, albums) = checkListCoverImage(data.albums.values, data)
      val (showErrors, shows) = checkListCoverImage(data.shows.values, data)
      val (tourErrors, tours) = checkListCoverImage(data.tours.values, data)
      val (mediaErrors, medias) = checkListCoverImage(data.medias.values, data)

      new Step4(
        Data(
          data.site,
          albums.map(e => (e.id, e)).toMap,
          songs.map(e => (e.id, e)).toMap,
          shows.map(e => (e.id, e)).toMap,
          tours.map(e => (e.id, e)).toMap,
          medias.map(e => (e.id, e)).toMap,
          data.pages,
          data.multimedia,
        ),
        errors ::: songErrors ::: albumErrors ::: showErrors ::: tourErrors ::: mediaErrors,
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
        case TourCoverImage(tourId) =>
          if (data.tours.contains(tourId)) {
            WithError(item)
          } else {
            WithError(item, DataError(item.id, s"Reference not found for cover image: tour '$tourId.id'"))
          }
        case SongCoverImage(songId) =>
          if (data.songs.contains(songId)) {
            WithError(item)
          } else {
            WithError(item, DataError(item.id, s"Reference not found for cover image: tour '$songId.id'"))
          }
      }
    }

  }

  class Step4(data: Data, errors: List[DataError]) {
    def expandRelatedTo(): Step5 = {
      val updatedData =
        List(
          addReferencedMultiMediaToRelatedToForAlbum,
          addReferencedMultiMediaToRelatedToForSong,
          addReferencedMultiMediaToRelatedToForShow,
          expandRelatedToFromAllToAll,
        )
          .foldLeft(data)((acc, f) => f(acc))

      Step5(updatedData, errors)
    }

    // TODO: generalize. When redoing Item type tree

    def expandRelatedToFromSongs(data: Data): Data = {
      data.songs.values.foldLeft(data) { (acc, song) => expandRelatedToFromSong(song, acc) }
    }

    def expandRelatedToFromSong(song: Song, data: Data): Data = {
      val updatedMultimedias = song.multimedia.all().flatMap(data.multimedia.get).map(_.withRelatedTo(song.id))
      data.copy(multimedia = data.multimedia ++ updatedMultimedias.map(m => (m.id, m)).toMap)
    }

    def expandRelatedToFromShows(data: Data): Data = {
      data.shows.values.foldLeft(data) { (acc, show) => expandRelatedToFromShow(show, acc) }
    }

    def expandRelatedToFromShow(show: Show, data: Data): Data = {
      val updatedMultimedias = show.multimedia.all().flatMap(data.multimedia.get).map(_.withRelatedTo(show.id))
      data.copy(multimedia = data.multimedia ++ updatedMultimedias.map(m => (m.id, m)).toMap)
    }

    def expandRelatedToFromAlbums(data: Data): Data = {
      data.albums.values.foldLeft(data) { (acc, album) => expandRelatedToFromAlbum(album, acc) }
    }

    def expandRelatedToFromAlbum(album: Album, data: Data): Data = {
      val updatedMultimedias = album.multimedia.all().flatMap(data.multimedia.get).map(_.withRelatedTo(album.id))
      data.copy(multimedia = data.multimedia ++ updatedMultimedias.map(m => (m.id, m)).toMap)
    }

    def addReferencedMultiMediaToRelatedToForAlbum(data: Data): Data = {
      val albums = data.albums.view.mapValues { a =>
        addReferencedMultiMediaToRelatedTo(a.multimedia.all(), a)
      }
      data.copy(albums = albums.toMap)
    }

    def addReferencedMultiMediaToRelatedToForSong(data: Data): Data = {
      val songs = data.songs.view.mapValues { s =>
        addReferencedMultiMediaToRelatedTo(s.multimedia.all(), s)
      }
      data.copy(songs = songs.toMap)
    }

    def addReferencedMultiMediaToRelatedToForShow(data: Data): Data = {
      val shows = data.shows.view.mapValues { s =>
        addReferencedMultiMediaToRelatedTo(s.multimedia.all(), s)
      }
      data.copy(shows = shows.toMap)
    }

    def addReferencedMultiMediaToRelatedTo[T <: Item[T]](multimedias: List[MultiMediaId], item: T): T = {
      // TODO: add a withRelatedTo(List) method, to avoid excessive calls and object creation
      multimedias.foldLeft(item)((i, m) => i.withRelatedTo(m))
    }

    def expandRelatedToFromAllToAll(data: Data): Data = {
      expandRelatedToToAll(
        data.multimedia.values,
        data,
      )
    }

    def expandRelatedToToAll(items: Iterable[Item[?]], data: Data): Data = {
      items.foldLeft(data) { (d, i) =>
        val res = i.relatedTo.foldLeft(d) { (d2, relTo) =>
          relTo match {
            case mId: MultiMediaId =>
              d2.copy(multimedia = d2.multimedia.updatedWith(mId)(_.map(_.withRelatedTo(i.id))))
            case aId: AlbumId =>
              d2.copy(albums = d2.albums.updatedWith(aId)(_.map(_.withRelatedTo(i.id))))
            case sId: ShowId =>
              d2.copy(shows = d2.shows.updatedWith(sId)(_.map(_.withRelatedTo(i.id))))
            case sId: SongId =>
              d2.copy(songs = d2.songs.updatedWith(sId)(_.map(_.withRelatedTo(i.id))))
            case tId: TourId =>
              d2.copy(tours = d2.tours.updatedWith(tId)(_.map(_.withRelatedTo(i.id))))
            case _ =>
              d2
          }
        }
        res
      }
    }
  }

  case class Step5(data: Data, errors: List[DataError]) {
    // check multimedia items against reference lists
    def checkReferenceLists(): Step6 = {
      val youtubevideoErrors = checkYoutubevideoReferenceList()
      val youtubeshortErrors = checkYoutubeshortReferenceList()

      Step6(data, errors ::: youtubevideoErrors ::: youtubeshortErrors)
    }

    def checkYoutubevideoReferenceList(): List[DataError] = {
      val definedYoutubevideoIds: Iterable[String] =
        data.multimedia.keys
          .flatMap {
            case YouTubeVideoId(id) =>
              Some(id)
            case _ =>
              None
          }

      checkReferenceList(data.site.youtubevideo, definedYoutubevideoIds, "YouTube video")(YouTubeVideoId.apply)
    }

    def checkYoutubeshortReferenceList(): List[DataError] = {
      val definedYoutubeshortIds: Iterable[String] =
        data.multimedia.keys
          .flatMap {
            case YouTubeShortId(id) =>
              Some(id)
            case _ =>
              None
          }

      checkReferenceList(data.site.youtubeshort, definedYoutubeshortIds, "YouTube short")(YouTubeVideoId.apply)
    }

    def checkReferenceList[T](refList: List[RefMediaIds], defList: Iterable[String], itemName: String)(
        idMaker: String => T
    ): List[DataError] = {
      val defined = TreeSet.from(defList)

      val errors = refList.foldLeft(Nil: List[DataError]) { (acc, refIds) =>
        refIds.ids.foldLeft(acc) { (acc2, refId) =>
          if (defined.contains(refId)) {
            defined.remove(refId)
            acc2
          } else {
            acc2 :+ DataError(
              YouTubeVideoId(refId),
              s"$itemName '$refId' from ${refIds.account} not defined",
              false,
            )
          }
        }
      }

      errors
        ::: defined.toList.map(id =>
          DataError(YouTubeVideoId(id), s"$itemName '$id' is defined, but not in the reference list", false)
        )
    }
  }

  class Step6(data: Data, errors: List[DataError]) {
    def done = (errors, data)
  }

  case class WithError[T](t: T, e: Option[DataError])

  object WithError {
    def apply[T](t: T): WithError[T] = WithError(t, None)
    def apply[T <: WithErrorSupport[T]](t: T, e: DataError): WithError[T] =
      WithError(if (e.fatal) t.errored() else t, Some(e))
  }

  case class WithErrors[+T](t: T, es: List[DataError])

  object WithErrors {
    // def apply[T](t: T): WithError[T] = WithError(t, Nil)
    def apply[T <: Item[T]](t: T, es: List[DataError]): WithErrors[T] =
      new WithErrors(if (es.exists(_.fatal)) t.errored() else t, es)
  }
}

object YamlFiles {

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
