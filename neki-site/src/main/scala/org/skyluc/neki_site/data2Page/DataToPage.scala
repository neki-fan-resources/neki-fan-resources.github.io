package org.skyluc.neki_site.data2Page

import org.skyluc.fan_resources.data.{Processor as _, *}
import org.skyluc.fan_resources.html.CompiledDataGenerator
import org.skyluc.fan_resources.html.Page
import org.skyluc.fan_resources.html.pages.CssPage
import org.skyluc.fan_resources.html.pages.SitemapPage
import org.skyluc.neki_site.data.*
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.pages.SongPage
import org.skyluc.neki_site.html.pages.{
  ChronologyPage as pChronologyPage,
  MusicPage as pMusicPage,
  ShowsPage as pShowsPage,
  *,
}

class DataToPage(generator: CompiledDataGenerator, static_pieces: Path, site: Site) extends Processor[Seq[SitePage]] {

  def generate(datums: Seq[Datum[?]]): Seq[Page] = {

    val cssStyles = CssPage(
      static_pieces.resolve("css"),
      Seq(
        Path("colors.css"),
        Path("referenceunit.css"),
        Path("pagelayout.css"),
        Path("pagestyle.css"),
        Path("maincontent.css"),
        Path("navbar.css"),
        Path("footer.css"),
        Path("aboutpage.css"),
        Path("bandpage.css"),
        Path("chronologypage.css"),
        Path("mediapages.css"),
        Path("sourcespage.css"),
        Path("updatespage.css"),
        Path("component", "coverimage.css"),
        Path("component", "largedetails.css"),
        Path("component", "linecard.css"),
        Path("component", "lyrics.css"),
        Path("component", "mediumcard.css"),
        Path("component", "mediumdetails.css"),
        Path("component", "multimediacard.css"),
        Path("component", "newsblock.css"),
        Path("component", "overlay.css"),
        Path("component", "smallcard.css"),
        Path("component", "socialmediacard.css"),
      ),
      "styles.css",
    )
    // fix pages
    val fixPages: Seq[SitePage] =
      Seq(
        AboutPage.pages(site),
        BandPage.pages(site),
        LivePage.pages(datums, site, generator),
        MediasPage.pages(datums, site, generator),
        SourcesPage.pages(datums, site, generator),
        UpdatesPage.pages(site),
      ).flatten
    // pages from datums (without errors)
    val res = datums.filterNot(_.hasError).map(_.process(this)).flatten

    val allPages = fixPages ++ res

    allPages :+ SitemapPage(allPages) :+ cssStyles
  }

  override def processAlbumMarker(albumMarker: AlbumMarker): Seq[SitePage] = NO_DATA

  override def processBaseMarker(baseMarker: BaseMarker): Seq[SitePage] = NO_DATA

  override def processGroup(group: Group): Seq[SitePage] = NO_DATA

  override def processMediaMarker(mediaMarker: MediaMarker): Seq[SitePage] = NO_DATA

  override def processMultiMediaMarker(multiMediaMarker: MultiMediaMarker): Seq[SitePage] =
    NO_DATA

  override def processShowMarker(showMarker: ShowMarker): Seq[SitePage] = NO_DATA

  override def processSongMarker(songMarker: SongMarker): Seq[SitePage] = NO_DATA

  override def processAlbum(album: Album): Seq[SitePage] =
    AlbumPage.pagesFor(album, site, generator)

  override def processLocalImage(localImage: LocalImage): Seq[SitePage] = NO_DATA

  override def processMediaAudio(mediaAudio: MediaAudio): Seq[SitePage] =
    MediaPage.pageFor(mediaAudio, site, generator)

  override def processMediaWritten(mediaWritten: MediaWritten): Seq[SitePage] =
    MediaPage.pageFor(mediaWritten, site, generator)

  override def processPostX(postX: PostX): Seq[SitePage] = NO_DATA

  override def processPostXImage(postXImage: PostXImage): Seq[SitePage] = NO_DATA

  override def processPostXVideo(postXVideo: PostXVideo): Seq[SitePage] = NO_DATA

  override def processShow(show: Show): Seq[SitePage] =
    ShowPage.pagesFor(show, site, generator)

  override def processSong(song: Song): Seq[SitePage] =
    SongPage.pagesFor(song, site, generator)

  override def processTour(tour: Tour): Seq[SitePage] =
    TourPage.pagesFor(tour, site, generator)

  override def processTourMarker(tourMarker: TourMarker): Seq[SitePage] = NO_DATA

  override def processYouTubeShort(youtubeShort: YouTubeShort): Seq[SitePage] = NO_DATA

  override def processYouTubeVideo(youtubeVideo: YouTubeVideo): Seq[SitePage] = NO_DATA

  override def processZaiko(zaiko: Zaiko): Seq[SitePage] = NO_DATA

  override def processChronologyPage(chronologyPage: ChronologyPage): Seq[SitePage] =
    pChronologyPage.pagesFor(chronologyPage, site, generator)

  override def processMusicPage(musicPage: MusicPage): Seq[SitePage] =
    pMusicPage.pagesFor(musicPage, site, generator)

  override def processSite(site: Site): Seq[SitePage] = NO_DATA

  override def processShowsPage(showsPage: ShowsPage): Seq[SitePage] =
    pShowsPage.pagesFor(showsPage, site, generator)

  // ----------

  val NO_DATA: Seq[SitePage] = Seq()

}
