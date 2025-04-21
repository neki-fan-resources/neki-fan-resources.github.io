package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor => _, _}
import org.skyluc.neki_site.data._
import org.skyluc.fan_resources.html.MultiMediaCompiledData
import org.skyluc.fan_resources.html.MultiMediaBlockCompiledData
import org.skyluc.fan_resources.html.Url
import org.skyluc.fan_resources.html.ImageWithOverlayCompiledData
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.html.ElementCompiledData

class MultiMediaCompiledDataGenerator(compilers: Compilers) extends Processor[MultiMediaCompiledData] {

  import MultiMediaCompiledDataGenerator._

  // TODO: add caching ?

  def get(datum: Datum[?]): MultiMediaCompiledData = {
    datum.process(this)
  }

  override def processAlbumMarker(albumMarker: AlbumMarker): MultiMediaCompiledData = ???

  override def processBaseMarker(baseMarker: BaseMarker): MultiMediaCompiledData = ???

  override def processMediaMarker(mediaMarker: MediaMarker): MultiMediaCompiledData = ???

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): MultiMediaCompiledData =
    ???

  override def processShowMarker(showMarker: ShowMarker): MultiMediaCompiledData = ???

  override def processSongMarker(songMarker: SongMarker): MultiMediaCompiledData = ???

  def get(id: Id[?]): MultiMediaCompiledData = {
    get(compilers.data.all(id))
  }

  def get(multimediaBlock: MultiMediaBlock, linkedTo: Seq[Id[?]]): MultiMediaBlockCompiledData = {
    MultiMediaBlockCompiledData(
      multimediaBlock.video.map(id => get(compilers.data.all(id))),
      multimediaBlock.live.map(id => get(compilers.data.all(id))),
      multimediaBlock.concert.map(id => get(compilers.data.all(id))),
      multimediaBlock.short.map(id => get(compilers.data.all(id))),
      multimediaBlock.image.map(id => get(compilers.data.all(id))),
      multimediaBlock.additional.map(id => get(compilers.data.all(id))),
      multimediaBlock.extra(linkedTo).map(id => get(compilers.data.all(id))),
    )
  }

  override def processAlbum(album: Album): MultiMediaCompiledData = ???

  override def processLocalImage(localImage: LocalImage): MultiMediaCompiledData = {
    val parent = compilers.data.all(localImage.id.itemId)

    val imageUrl = CoverImage.resolveImageAsset(localImage.filename, parent)

    MultiMediaCompiledData(
      LocalImage.DESIGNATION,
      localImage.label,
      localImage.publishedDate,
      ImageWithOverlayCompiledData(
        imageUrl,
        OVERLAY_LOCAL_IMAGE_SOURCE,
        localImage.label + IMAGE_ALT,
        OVERLAY_LOCAL_IMAGE_ALT,
        false,
      ),
      None,
      Nil,
      true,
      imageUrl,
    )

  }

  override def processMediaAudio(mediaAudio: MediaAudio): MultiMediaCompiledData = ???

  override def processMediaWritten(mediaWritten: MediaWritten): MultiMediaCompiledData = ???

  override def processPostX(postX: PostX): MultiMediaCompiledData = ???

  override def processPostXImage(postXImage: PostXImage): MultiMediaCompiledData = {
    val postXCompiledData = compilers.elementDataCompiler.get(PostXId(postXImage.id.postId))

    val imageUrl = Url(PostX.IMAGE_BASE_URL_1, postXImage.id.imageId, PostX.IMAGE_BASE_URL_2)

    MultiMediaCompiledData(
      PostX.DESIGNATION_IMAGE,
      postXImage.label,
      postXImage.publishedDate,
      ImageWithOverlayCompiledData(
        imageUrl,
        OVERLAY_X_POST_IMAGE,
        postXImage.label + IMAGE_ALT,
        OVERLAY_X_POST_IMAGE_ALT,
        false,
      ),
      postXImage.info,
      Nil, // TODO: what makes sense ?
      true,
      postXCompiledData.targetUrl,
    )

  }

  override def processShow(show: Show): MultiMediaCompiledData = ???

  override def processSong(song: Song): MultiMediaCompiledData = ???

  override def processTour(tour: Tour): MultiMediaCompiledData = ???

  override def processYouTubeShort(youtubeShort: YouTubeShort): MultiMediaCompiledData = {
    val targetUrl = Url(URL_YOUTUBE_SHORT, youtubeShort.id.id)
    val imageUrl = Url(IMAGE_URL_YOUTUBE_VIDEO_1, youtubeShort.id.id, IMAGE_URL_YOUTUBE_SHORT_2)

    MultiMediaCompiledData(
      DESIGNATION_YOUTUBE_SHORT,
      youtubeShort.label,
      youtubeShort.publishedDate,
      ImageWithOverlayCompiledData(
        imageUrl,
        OVERLAY_YOUTUBE_SOURCE,
        youtubeShort.label + IMAGE_ALT,
        OVERLAY_YOUTUBE_ALT,
        true,
      ),
      youtubeShort.info,
      generateFroms(youtubeShort.linkedTo),
      true,
      targetUrl,
    )

  }

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): MultiMediaCompiledData = {
    val targetUrl = Url(URL_YOUTUBE_VIDEO, youtubeVideo.id.id)
    val imageUrl = Url(IMAGE_URL_YOUTUBE_VIDEO_1, youtubeVideo.id.id, IMAGE_URL_YOUTUBE_VIDEO_2)

    MultiMediaCompiledData(
      DESIGNATION_YOUTUBE_VIDEO,
      youtubeVideo.label,
      youtubeVideo.publishedDate,
      ImageWithOverlayCompiledData(
        imageUrl,
        OVERLAY_YOUTUBE_SOURCE,
        youtubeVideo.label + IMAGE_ALT,
        OVERLAY_YOUTUBE_ALT,
        true,
      ),
      None, // not supported on YouTubeVideo yet
      generateFroms(youtubeVideo.linkedTo),
      true,
      targetUrl,
    )

  }

  override def processZaiko(zaiko: Zaiko): MultiMediaCompiledData = {
    val info = "available from " + zaiko.publishedDate.toString() +
      zaiko.expirationDate.map(d => " until " + d.toString()).getOrElse(Common.EMPTY)
    val available = zaiko.expirationDate.map(!_.isPast()).getOrElse(true)

    val targetUrl = Url(Zaiko.BASE_URL_1, zaiko.id.channel, Zaiko.BASE_URL_2, zaiko.id.id)

    MultiMediaCompiledData(
      Zaiko.DESIGNATION,
      zaiko.label,
      zaiko.publishedDate,
      ImageWithOverlayCompiledData(
        Url(zaiko.coverImage),
        OVERLAY_ZAIKO_SOURCE,
        zaiko.label + IMAGE_ALT,
        OVERLAY_ZAIKO_ALT,
        true,
      ),
      Some(info),
      generateFroms(zaiko.linkedTo),
      available,
      targetUrl,
    )

  }

  override def processChronologyPage(chronologyPage: ChronologyPage): MultiMediaCompiledData = ???

  override def processMusicPage(musicPage: MusicPage): MultiMediaCompiledData = ???

  override def processSite(site: Site): MultiMediaCompiledData = ???

  override def processShowsPage(showsPage: ShowsPage): MultiMediaCompiledData = ???

  private def generateFroms(ids: Seq[Id[?]]): Seq[(String, ElementCompiledData)] = {
    ids.map { id =>
      val compiledData = compilers.elementDataCompiler.get(id)
      (compiledData.fromKey, compiledData)
    }
  }

}

object MultiMediaCompiledDataGenerator {

  val OVERLAY_PATH = Path("asset", "image", "overlay")
  val IMAGE_ALT = " media image"

  // YouTube video and short
  val DESIGNATION_YOUTUBE_VIDEO = "YouTube video"
  val DESIGNATION_YOUTUBE_SHORT = "YouTube video"
  val OVERLAY_YOUTUBE_SOURCE = Url(OVERLAY_PATH.resolve("youtube.svg"))
  val OVERLAY_YOUTUBE_ALT = "YouTube logo"
  val URL_YOUTUBE_VIDEO = "https://www.youtube.com/watch?v="
  val URL_YOUTUBE_SHORT = "https://www.youtube.com/shorts/"
  val IMAGE_URL_YOUTUBE_VIDEO_1 = "https://i.ytimg.com/vi/"
  val IMAGE_URL_YOUTUBE_VIDEO_2 = "/mqdefault.jpg"
  val IMAGE_URL_YOUTUBE_SHORT_2 = "/oardefault.jpg"

  // X post
  val OVERLAY_X_POST_IMAGE = Url(OVERLAY_PATH.resolve("x.ico"))
  val OVERLAY_X_POST_IMAGE_ALT = "X logo"

  // Zaiko
  val OVERLAY_ZAIKO_SOURCE = Url(OVERLAY_PATH.resolve("zaiko.ico"))
  val OVERLAY_ZAIKO_ALT = "Zaiko logo"

  // local image

  val OVERLAY_LOCAL_IMAGE_SOURCE = Url(OVERLAY_PATH.resolve("empty.png"))
  val OVERLAY_LOCAL_IMAGE_ALT = "empty"

}
