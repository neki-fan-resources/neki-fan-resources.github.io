package org.skyluc.fan_resources.data

trait Processor[T] {

  def processAlbum(album: Album): T

  def processAlbumMarker(albumMarker: AlbumMarker): T

  def processBaseMarker(baseMarker: BaseMarker): T

  def processLocalImage(localImage: LocalImage): T

  def processMediaAudio(mediaAudio: MediaAudio): T

  def processMediaMarker(mediaMarker: MediaMarker): T

  def processMediaWritten(mediaWritten: MediaWritten): T

  def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): T

  def processPostX(postX: PostX): T

  def processPostXImage(postXImage: PostXImage): T

  def processShow(show: Show): T

  def processShowMarker(showMarker: ShowMarker): T

  def processSong(song: Song): T

  def processSongMarker(songMarker: SongMarker): T

  def processTour(tour: Tour): T

  def processYouTubeShort(youtubeShort: YouTubeShort): T

  def processYouTubeVideo(youtubeVideo: YouTubeVideo): T

  def processZaiko(zaiko: Zaiko): T

}
