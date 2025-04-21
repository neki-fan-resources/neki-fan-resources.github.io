package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.{Processor => _, _}
import org.skyluc.neki_site.data._
import org.skyluc.fan_resources.html.MarkerCompiledData
import org.skyluc.fan_resources.html.MarkerCompiledDataMarker
import org.skyluc.fan_resources.html.ImageCompiledData
import org.skyluc.neki_site.html.pages.ChronologySvg
import org.skyluc.fan_resources.html.MarkerCompiledDataDetails
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.html.Url

class ChronoloyMarkerProcessor(refDay: Int, compilers: Compilers) extends Processor[MarkerCompiledData] {

  override def processAlbum(album: Album): MarkerCompiledData = ???

  override def processAlbumMarker(albumMarker: AlbumMarker): MarkerCompiledData = {

    val albumCompiledData = compilers.elementDataCompiler.get(albumMarker.id.albumId)

    MarkerCompiledData(
      albumMarker.id.uId(),
      MarkerCompiledDataMarker(
        Some(albumCompiledData.designation),
        albumCompiledData.shortLabel.getOrElse(albumCompiledData.label),
        albumCompiledData.sublabel,
        albumCompiledData.cover,
        albumCompiledData.date.fromRefDay(refDay),
        ChronologySvg.CLASS_ALBUM_MARKER,
        false,
        false,
        albumMarker.position.up,
        albumMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_ELEMENT,
        Some(albumCompiledData),
        None,
        albumCompiledData.fromKey,
      ),
    )

  }

  override def processBaseMarker(baseMarker: BaseMarker): MarkerCompiledData = {

    val cover = ImageCompiledData(Url(baseMarker.image), baseMarker.label + " cover", None)

    MarkerCompiledData(
      baseMarker.id.uId(),
      MarkerCompiledDataMarker(
        None,
        baseMarker.label,
        None,
        cover,
        baseMarker.date.fromRefDay(refDay),
        ChronologySvg.CLASS_BASE_MARKER,
        true,
        false,
        baseMarker.position.up,
        baseMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_BASIC,
        Some(
          ElementCompiledData(
            "",
            baseMarker.label,
            None,
            None,
            None,
            None,
            baseMarker.date,
            None,
            cover,
            Nil,
            Url(Common.EMPTY),
            Common.EMPTY,
          )
        ),
        baseMarker.relatedMultimedia.map(compilers.multimediaDataCompiler.get),
        Common.EMPTY,
      ),
    )

  }

  override def processLocalImage(localImage: LocalImage): MarkerCompiledData = ???

  override def processMediaAudio(mediaAudio: MediaAudio): MarkerCompiledData = ???

  override def processMediaMarker(mediaMarker: MediaMarker): MarkerCompiledData = {

    val mediaCompiledData = compilers.elementDataCompiler.get(mediaMarker.id.mediaId)

    MarkerCompiledData(
      mediaMarker.id.uId(),
      MarkerCompiledDataMarker(
        Some(mediaCompiledData.designation),
        mediaCompiledData.shortLabel.getOrElse(mediaCompiledData.label),
        mediaCompiledData.sublabel,
        mediaCompiledData.cover,
        mediaCompiledData.date.fromRefDay(refDay),
        ChronologySvg.CLASS_MEDIA_MARKER,
        true,
        mediaMarker.short,
        mediaMarker.position.up,
        mediaMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_ELEMENT,
        Some(mediaCompiledData),
        None,
        mediaCompiledData.fromKey,
      ),
    )

  }

  override def processMediaWritten(mediaWritten: MediaWritten): MarkerCompiledData = ???

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): MarkerCompiledData = {
    val multimediaCompiledData = compilers.multimediaDataCompiler.get(multiMediaMarker.id.multimediaId)

    val parent = multimediaCompiledData.froms.find(_._1 == multiMediaMarker.parentKey).map(_._2)

    val (label, sublabel) = parent
      .map(p => (p.label, Some(multimediaCompiledData.label)))
      .getOrElse((multimediaCompiledData.label, None))

    MarkerCompiledData(
      multiMediaMarker.id.uId().replace('-', '_'),
      MarkerCompiledDataMarker(
        Some(multimediaCompiledData.designation),
        label,
        sublabel,
        ImageCompiledData(multimediaCompiledData.image),
        multimediaCompiledData.date.fromRefDay(refDay),
        ChronologySvg.CLASS_MULTIMEDIA_MARKER,
        false,
        false,
        multiMediaMarker.position.up,
        multiMediaMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_MULTIMEDIA,
        parent,
        Some(multimediaCompiledData),
        multiMediaMarker.parentKey,
      ),
    )

  }

  override def processPostX(postX: PostX): MarkerCompiledData = ???

  override def processPostXImage(postXImage: PostXImage): MarkerCompiledData = ???

  override def processShow(show: Show): MarkerCompiledData = ???

  override def processShowMarker(showMarker: ShowMarker): MarkerCompiledData = {

    val showCompiledData = compilers.elementDataCompiler.get(showMarker.id.showId)

    MarkerCompiledData(
      showMarker.id.uId(),
      MarkerCompiledDataMarker(
        Some(showCompiledData.designation),
        showCompiledData.shortLabel.getOrElse(showCompiledData.label),
        showCompiledData.sublabel,
        showCompiledData.cover,
        showCompiledData.date.fromRefDay(refDay),
        ChronologySvg.CLASS_SHOW_MARKER,
        true,
        showMarker.short,
        showMarker.position.up,
        showMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_ELEMENT,
        Some(showCompiledData),
        showMarker.relatedMultimedia.map(compilers.multimediaDataCompiler.get),
        showCompiledData.fromKey,
      ),
    )

  }

  override def processSong(song: Song): MarkerCompiledData = ???

  override def processSongMarker(songMarker: SongMarker): MarkerCompiledData = {

    val songCompiledData = compilers.elementDataCompiler.get(songMarker.id.songId)

    MarkerCompiledData(
      songMarker.id.uId(),
      MarkerCompiledDataMarker(
        Some(songCompiledData.designation),
        songCompiledData.shortLabel.getOrElse(songCompiledData.label),
        songCompiledData.sublabel,
        songCompiledData.cover,
        songCompiledData.date.fromRefDay(refDay),
        ChronologySvg.CLASS_SHOW_MARKER,
        false,
        false,
        songMarker.position.up,
        songMarker.position.in,
      ),
      MarkerCompiledDataDetails(
        MarkerCompiledData.DETAILS_ELEMENT,
        Some(songCompiledData),
        songMarker.relatedMultimedia.map(compilers.multimediaDataCompiler.get),
        songCompiledData.fromKey,
      ),
    )

  }

  override def processTour(tour: Tour): MarkerCompiledData = ???

  override def processYouTubeShort(youtubeShort: YouTubeShort): MarkerCompiledData = ???

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): MarkerCompiledData = ???

  override def processZaiko(zaiko: Zaiko): MarkerCompiledData = ???

  override def processChronologyPage(chronologyPage: ChronologyPage): MarkerCompiledData = ???

  override def processMusicPage(musicPage: MusicPage): MarkerCompiledData = ???

  override def processSite(site: Site): MarkerCompiledData = ???

  override def processShowsPage(showsPage: ShowsPage): MarkerCompiledData = ???

  def process(datum: Datum[?]): MarkerCompiledData = {
    datum.process(this)
  }

}
