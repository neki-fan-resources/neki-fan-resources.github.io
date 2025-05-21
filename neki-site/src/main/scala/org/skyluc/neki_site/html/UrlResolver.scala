package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html as fr
import org.skyluc.fan_resources.html.Url
import org.skyluc.neki_site.data.*

object UrlResolver extends fr.UrlResolver with Processor[Url] {

  def resolve(datum: Datum[?]): Url = {
    datum.process(this)
  }

  override def processAlbumMarker(albumMarker: AlbumMarker): Url = ???

  override def processBaseMarker(baseMarker: BaseMarker): Url = ???

  override def processGroup(group: Group): Url = ???

  override def processMediaMarker(mediaMarker: MediaMarker): Url = ???

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Url = ???

  override def processShowMarker(showMarker: ShowMarker): Url = ???

  override def processSongMarker(songMarker: SongMarker): Url = ???

  override def processChronologyPage(chronologyPage: ChronologyPage): Url = ???

  override def processMusicPage(musicPage: MusicPage): Url = ???

  override def processSite(site: Site): Url = ???

  override def processShowsPage(showsPage: ShowsPage): Url = ???

  override def processAlbum(album: Album): Url = generateBasic(album)

  override def processLocalImage(localImage: LocalImage): Url = ???

  override def processMediaAudio(mediaAudio: MediaAudio): Url = generateBasic(mediaAudio)

  override def processMediaWritten(mediaWritten: MediaWritten): Url = generateBasic(mediaWritten)

  override def processPostX(postX: PostX): Url = ???

  override def processPostXImage(postXImage: PostXImage): Url = ???

  override def processPostXVideo(postXVideo: PostXVideo): Url = ???

  override def processShow(show: Show): Url = generateBasic(show)

  override def processSong(song: Song): Url = generateBasic(song)
  override def processTour(tour: Tour): Url = generateBasic(tour)

  override def processTourMarker(tourMarker: TourMarker): Url = ???

  override def processYouTubeShort(youtubeShort: YouTubeShort): Url = ???

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Url = ???

  override def processZaiko(zaiko: Zaiko): Url = ???

  private def generateBasic(datum: Datum[?]): Url =
    if (datum.id.dark) {
      Url(SitePage.DARK_PATH.resolve(datum.id.path).withExtension(".html"))
    } else {
      Url(datum.id.path.withExtension(".html"))
    }
}
