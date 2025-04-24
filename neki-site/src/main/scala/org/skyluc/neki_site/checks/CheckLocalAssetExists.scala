package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.neki_site.Main
import org.skyluc.neki_site.checks.DataCheck.Acc
import org.skyluc.neki_site.data.{Site as dSite, *}
import org.skyluc.neki_site.html.Site

import java.nio.file.Files
import java.nio.file.Path as fPath

object CheckLocalAssetExists extends Processor[Seq[CheckError]] {

  def process(data: Data): (Seq[CheckError], Data) = {

    val res = data.all.values.foldLeft(Acc.EMPTY) { (acc, datum) =>

      val errors = datum.process(this)
      Acc(errors ++ acc.errors, datum :: acc.datums)
    }

    (res.errors, data.withDatums(res.datums))
  }

  override def processChronologyPage(chronologyPage: ChronologyPage): Seq[CheckError] = Nil

  override def processMusicPage(musicPage: MusicPage): Seq[CheckError] = Nil

  override def processSite(site: dSite): Seq[CheckError] = Nil

  override def processShowsPage(showsPage: ShowsPage): Seq[CheckError] = Nil

  override def processAlbum(album: Album): Seq[CheckError] = Nil

  override def processAlbumMarker(albumMarker: AlbumMarker): Seq[CheckError] = Nil

  // TODO-NOW: this is the only one not using a local image
  override def processBaseMarker(baseMarker: BaseMarker): Seq[CheckError] = {
    // strip leading '/'
    val relativeFilename = if (baseMarker.image.startsWith("/")) {
      baseMarker.image.substring(1)
    } else {
      baseMarker.image
    }
    val path = fPath.of(Main.STATIC_PATH).resolve(fPath.of(relativeFilename))

    if (Files.isRegularFile(path)) {
      Nil
    } else {
      Seq(CheckError(baseMarker.id, s"referenced local asset '${baseMarker.image}' is not found"))
    }
  }

  override def processLocalImage(localImage: LocalImage): Seq[CheckError] = {
    val imagePath = Path(Main.STATIC_PATH)
      .resolve(Site.BASE_IMAGE_ASSET_PATH)
      .resolve(localImage.id.itemId.path)
      .resolve(localImage.filename)
    if (Files.isRegularFile(imagePath.asFilePath())) {
      Nil
    } else {
      Seq(CheckError(localImage.id, s"referenced local asset '${localImage.filename}' is not found"))
    }
  }

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[CheckError] = Nil

  override def processMediaMarker(mediaMarker: MediaMarker): Seq[CheckError] = Nil

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[CheckError] = Nil

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Seq[CheckError] = Nil

  override def processPostX(postX: PostX): Seq[CheckError] = Nil

  override def processPostXImage(postXImage: PostXImage): Seq[CheckError] = Nil

  override def processShow(show: Show): Seq[CheckError] = Nil

  override def processShowMarker(showMarker: ShowMarker): Seq[CheckError] = Nil

  override def processSong(song: Song): Seq[CheckError] = Nil

  override def processSongMarker(songMarker: SongMarker): Seq[CheckError] = Nil

  override def processTour(tour: Tour): Seq[CheckError] = Nil

  override def processYouTubeShort(youtubeShort: YouTubeShort): Seq[CheckError] = Nil

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Seq[CheckError] = Nil

  override def processZaiko(zaiko: Zaiko): Seq[CheckError] = Nil

}
