package org.skyluc.neki_site.html

import org.skyluc.fan_resources.MainSiteConfiguration
import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.Config
import org.skyluc.neki_site.data.ProcessorElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.page.MainSitePage

import d.Path
import fr.page.PostXImagePage

object ElementToPage {
  val NO_PAGES = Seq[MainSitePage]()

  def generate(data: d.Data, configuration: MainSiteConfiguration): Seq[fr.page.Page] = {

    val cssPages = generateCssPages(configuration)

    val generator = fr.compileddata.CompiledDataGenerator(data, compileddata.CompiledDataGeneratorProcessorsBuilder)

    val processor = Processor(generator)
    val elementPages = data.elements.view.values.flatMap(_.process(processor)).toSeq

    val site = data.get(Site.ID)

    val singlePages = Seq(
      page.AboutPage
    )
      ++ page.BandPage.pagesFor(site, generator)
      ++ page.SourcesPage.pagesFor(data, generator)

    val indexedPages = elementPages ++ singlePages

    val sitemapPage = fr.page.SitemapPage(indexedPages)

    val postximagePage = PostXImagePage(MainSitePage.DOMAIN_NAME, MainSitePage.CSS_PATHS, Config.current.isLocal)

    indexedPages ++ cssPages :+ postximagePage :+ sitemapPage
  }

  private def generateCssPages(configuration: MainSiteConfiguration): Seq[fr.page.Page] = {
    Seq(
      fr.page.CssFile(
        configuration.frStaticPiecesFolder.resolve("css"),
        Seq(
          Path("referenceunit.css"),
          Path("mainsitepage.css"),
          Path("maincontent.css"),
          Path("component", "chronology.css"),
          Path("component", "cover.css"),
          Path("component", "elementupdatessection.css"),
          Path("component", "footer.css"),
          Path("component", "header.css"),
          Path("component", "kofi.css"),
          Path("component", "largedetails.css"),
          Path("component", "linecard.css"),
          Path("component", "lyrics.css"),
          Path("component", "markercard.css"),
          Path("component", "mediumdetails.css"),
          Path("component", "multimediacard.css"),
          Path("component", "newsblock.css"),
          Path("component", "overlay.css"),
          Path("component", "postximagepage.css"),
          Path("component", "status.css"),
          Path("component", "socialmediacard.css"),
          Path("component", "summarysection.css"),
          Path("component", "tabs.css"),
        ),
        "fr-styles.css",
      ),
      fr.page.CssFile(
        configuration.mainStaticPiecesFolder.resolve("css"),
        Seq(
          Path("colors.css"),
          Path("aboutpage.css"),
          Path("bandpage.css"),
          Path("sourcespage.css"),
          Path("overwrites.css"),
        ),
        "styles.css",
      ),
    )
  }

  class Processor(generator: fr.compileddata.CompiledDataGenerator)
      extends ProcessorElement[Seq[fr.page.MainSitePage]] {

    override def processAlbum(album: d.Album): Seq[fr.page.MainSitePage] =
      page.AlbumPage.pagesFor(album, generator)

    override def processCategoriesPage(categoriesPage: d.CategoriesPage): Seq[fr.page.MainSitePage] =
      page.CategoriesPage.pagesFor(categoriesPage, generator)

    override def processContentPage(contentPage: d.ContentPage): Seq[fr.page.MainSitePage] =
      page.ContentPage.pagesFor(contentPage, generator)

    override def processEvent(event: d.Event): Seq[fr.page.MainSitePage] =
      page.EventPage.pagesFor(event, generator)

    override def processMediaAudio(mediaAudio: d.MediaAudio): Seq[fr.page.MainSitePage] =
      page.MediaPage.pagesFor(mediaAudio, generator)

    override def processMediaVideo(mediaVideo: d.MediaVideo): Seq[fr.page.MainSitePage] =
      page.MediaPage.pagesFor(mediaVideo, generator)

    override def processMediaWritten(mediaWritten: d.MediaWritten): Seq[fr.page.MainSitePage] =
      page.MediaPage.pagesFor(mediaWritten, generator)

    override def processMultiMediaEvent(multimediaEvent: d.MultiMediaEvent): Seq[fr.page.MainSitePage] = Nil

    override def processShow(show: d.Show): Seq[fr.page.MainSitePage] =
      page.ShowPage.pagesFor(show, generator)

    override def processSong(song: d.Song): Seq[fr.page.MainSitePage] =
      page.SongPage.pagesFor(song, generator)

    override def processTour(tour: d.Tour): Seq[fr.page.MainSitePage] =
      page.TourPage.pagesFor(tour, generator)

    override def processUpdatePage(updatePage: d.UpdatePage): Seq[fr.page.MainSitePage] =
      page.UpdatePage.pagesFor(updatePage, generator)

  }

}
