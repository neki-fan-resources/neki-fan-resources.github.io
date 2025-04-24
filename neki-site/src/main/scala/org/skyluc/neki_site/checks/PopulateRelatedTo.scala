package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.data.{Data as _, Processor as _, *}
import org.skyluc.neki_site.data.*

object PopulateRelatedTo extends Processor[Datum[?]] {

  // TODO: add references from the cover images

  def process(data: Data): Data = {
    val newDatums = data.all.values.map(_.process(this))
    data.withDatums(newDatums)
  }

  override def processAlbum(album: Album): Datum[?] = {
    val references: Seq[Id[?]] =
      album.songs ++
        album.multimedia.all()

    album.withLinkedTo(references*)
  }

  override def processAlbumMarker(albumMarker: AlbumMarker): Datum[?] = {
    val references: Seq[Id[?]] = Seq(albumMarker.id.albumId)

    albumMarker.withLinkedTo(references*)
  }

  override def processBaseMarker(baseMarker: BaseMarker): Datum[?] = {
    val references: Seq[Id[?]] = baseMarker.relatedMultimedia.toSeq

    baseMarker.withLinkedTo(references*)
  }

  override def processLocalImage(localImage: LocalImage): Datum[?] = {
    localImage
  }

  override def processMediaAudio(mediaAudio: MediaAudio): Datum[?] = {
    val references: Seq[Id[?]] = mediaAudio.multimedia.all()

    mediaAudio.withLinkedTo(references*)
  }

  override def processMediaMarker(mediaMarker: MediaMarker): Datum[?] = {
    val references: Seq[Id[?]] = Seq(mediaMarker.id.mediaId)

    mediaMarker.withLinkedTo(references*)
  }

  override def processMediaWritten(mediaWritten: MediaWritten): Datum[?] = {
    val references: Seq[Id[?]] = mediaWritten.multimedia.all()

    mediaWritten.withLinkedTo(references*)
  }

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Datum[?] = {
    // TOOD: dereference parent from the parent key
    val references: Seq[Id[?]] = Seq(multiMediaMarker.id.multimediaId)

    multiMediaMarker.withLinkedTo(references*)
  }

  override def processPostX(postX: PostX): Datum[?] = {
    postX
  }

  override def processPostXImage(postXImage: PostXImage): Datum[?] = {
    postXImage
  }

  override def processShow(show: Show): Datum[?] = {
    val references: Seq[Id[?]] =
      show.tour.toSeq
        ++ show.multimedia.all()

    show.withLinkedTo(references*)
  }

  override def processShowMarker(showMarker: ShowMarker): Datum[?] = {
    val references: Seq[Id[?]] =
      Seq(showMarker.id.showId)
        ++ showMarker.relatedMultimedia.toSeq

    showMarker.withLinkedTo(references*)
  }

  override def processSong(song: Song): Datum[?] = {
    val references: Seq[Id[?]] =
      song.album.toSeq
        ++ song.multimedia.all()

    song.withLinkedTo(references*)
  }

  override def processSongMarker(songMarker: SongMarker): Datum[?] = {
    val references: Seq[Id[?]] =
      Seq(songMarker.id.songId)
        ++ songMarker.relatedMultimedia.toSeq

    songMarker.withLinkedTo(references*)
  }

  override def processTour(tour: Tour): Datum[?] = {
    val references: Seq[Id[?]] =
      tour.shows

    tour.withLinkedTo(references*)
  }

  override def processYouTubeShort(youtubeShort: YouTubeShort): Datum[?] = {
    youtubeShort
  }

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Datum[?] = {
    youtubeVideo
  }

  override def processZaiko(zaiko: Zaiko): Datum[?] = {
    zaiko
  }

  override def processChronologyPage(chronologyPage: ChronologyPage): Datum[?] = {
    chronologyPage
  }

  override def processMusicPage(musicPage: MusicPage): Datum[?] = {
    val references: Seq[Id[?]] = musicPage.music

    musicPage.withLinkedTo(references*)
  }

  override def processSite(site: Site): Datum[?] = {
    site
  }

  override def processShowsPage(showsPage: ShowsPage): Datum[?] = {
    val references: Seq[Id[?]] = showsPage.shows

    showsPage.withLinkedTo(references*)
  }

}
