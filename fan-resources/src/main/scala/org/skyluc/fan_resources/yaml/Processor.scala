package org.skyluc.fan_resources.yaml

trait Processor[E, A] {
  def processAlbum(album: Album): Either[E, A]
  def processLocalImage(localImage: LocalImage): Either[E, A]
  def processMediaAudio(mediaAudio: MediaAudio): Either[E, A]
  def processMediaWritten(mediaWritten: MediaWritten): Either[E, A]
  def processPostX(postX: PostX): Either[E, A]
  def processShow(show: Show): Either[E, A]
  def processSong(song: Song): Either[E, A]
  def processTour(tour: Tour): Either[E, A]
  def processYouTubeShort(youtubeShort: YouTubeShort): Either[E, A]
  def processYouTubeVideo(youtubeVideo: YouTubeVideo): Either[E, A]
  def processZaiko(zaiko: Zaiko): Either[E, A]
}
