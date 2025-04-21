package org.skyluc.neki_site.data2Page

import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.neki_site.data.*
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.pages.SongPage
import org.skyluc.neki_site.html.pages.{
  ChronologyPage as pChronologyPage,
  MusicPage as pMusicPage,
  ShowsPage as pShowsPage,
  *,
}

class DataToPage(compilers: Compilers) extends Processor[Seq[SitePage]] {

  def generate(datums: Seq[Datum[?]]): Seq[SitePage] = {
    // fix pages
    val fixPages: Seq[SitePage] =
      Seq(
        AboutPage.pages(compilers),
        BandPage.pages(compilers),
        LivePage.pages(compilers),
        MediasPage.pages(compilers),
        SourcesPage.pages(compilers),
      ).flatten
    // pages from datums
    val res = datums.map(_.process(this)).flatten

    fixPages ++ res
  }

  override def processAlbumMarker(albumMarker: AlbumMarker): Seq[SitePage] = NO_DATA

  override def processBaseMarker(baseMarker: BaseMarker): Seq[SitePage] = NO_DATA

  override def processMediaMarker(mediaMarker: MediaMarker): Seq[SitePage] = NO_DATA

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Seq[SitePage] =
    NO_DATA

  override def processShowMarker(showMarker: ShowMarker): Seq[SitePage] = NO_DATA

  override def processSongMarker(songMarker: SongMarker): Seq[SitePage] = NO_DATA

  override def processAlbum(album: Album): Seq[SitePage] =
    AlbumPage.pagesFor(album, compilers)

  override def processLocalImage(localImage: LocalImage): Seq[SitePage] = NO_DATA

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[SitePage] =
    MediaPage.pageFor(mediaAudio, compilers)

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[SitePage] =
    MediaPage.pageFor(mediaWritten, compilers)

  override def processPostX(postX: PostX): Seq[SitePage] = NO_DATA

  override def processPostXImage(postXImage: PostXImage): Seq[SitePage] = NO_DATA

  override def processShow(show: Show): Seq[SitePage] =
    ShowPage.pagesFor(show, compilers)

  override def processSong(song: Song): Seq[SitePage] =
    SongPage.pagesFor(song, compilers)

  override def processTour(tour: Tour): Seq[SitePage] =
    TourPage.pagesFor(tour, compilers)

  override def processYouTubeShort(youtubeShort: YouTubeShort): Seq[SitePage] = NO_DATA

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Seq[SitePage] = NO_DATA

  override def processZaiko(zaiko: Zaiko): Seq[SitePage] = NO_DATA

  override def processChronologyPage(chronologyPage: ChronologyPage): Seq[SitePage] =
    pChronologyPage.pagesFor(chronologyPage, compilers)

  override def processMusicPage(musicPage: MusicPage): Seq[SitePage] =
    pMusicPage.pagesFor(musicPage, compilers)

  override def processSite(site: Site): Seq[SitePage] = NO_DATA

  override def processShowsPage(showsPage: ShowsPage): Seq[SitePage] =
    pShowsPage.pagesFor(showsPage, compilers)

  // ----------

  val NO_DATA: Seq[SitePage] = Seq()

}
