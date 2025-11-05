package org.skyluc.neki_site.html

import org.skyluc.fan_resources.MainSiteConfiguration
import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.Config
import org.skyluc.neki_site.data.ProcessorElement
import org.skyluc.neki_site.data.Site
import org.skyluc.neki_site.html.page.MainSitePage
import org.skyluc.neki_site.html.page.MainSitePageBuilder

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
      ++ fr.page.UpdatePagePage.pagesFor(generator, page.MainSitePageBuilder)

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
          Path("component", "content.css"),
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
          Path("component", "textsection.css"),
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
      fr.page.element.AlbumBlockPages(album).build(page.ElementContentPageBuilder, generator)

    override def processCategoriesPage(categoriesPage: d.CategoriesPage): Seq[fr.page.MainSitePage] =
      fr.page.CategoriesPagePage.pagesFor(categoriesPage, generator, MainSitePageBuilder)

    override def processContentPage(contentPage: d.ContentPage): Seq[fr.page.MainSitePage] =
      if (contentPage.displayType == fr.component.ChronologySectionConfiguration.DISPLAY_TYPE_TIMELINE) {
        page.TimelinePage.pagesFor(contentPage, generator)
      } else {
        fr.page.ContentPagePage.pagesFor(contentPage, generator, MainSitePageBuilder)
      }

    override def processEvent(event: d.Event): Seq[fr.page.MainSitePage] =
      fr.page.element.EventBlockPages(event).build(page.ElementContentPageBuilder, generator)

    override def processMediaAudio(mediaAudio: d.MediaAudio): Seq[fr.page.MainSitePage] =
      fr.page.element.MediaBlockPages(mediaAudio).build(page.ElementContentPageBuilder, generator)

    override def processMediaVideo(mediaVideo: d.MediaVideo): Seq[fr.page.MainSitePage] =
      fr.page.element.MediaBlockPages(mediaVideo).build(page.ElementContentPageBuilder, generator)

    override def processMediaWritten(mediaWritten: d.MediaWritten): Seq[fr.page.MainSitePage] =
      fr.page.element.MediaBlockPages(mediaWritten).build(page.ElementContentPageBuilder, generator)

    override def processMultiMediaEvent(multimediaEvent: d.MultiMediaEvent): Seq[fr.page.MainSitePage] = Nil

    override def processShow(show: d.Show): Seq[fr.page.MainSitePage] =
      fr.page.element.ShowBlockPages(show).build(page.ElementContentPageBuilder, generator)

    override def processSong(song: d.Song): Seq[fr.page.MainSitePage] =
      fr.page.element.SongBlockPages(song).build(page.ElementContentPageBuilder, generator)

    override def processTour(tour: d.Tour): Seq[fr.page.MainSitePage] =
      fr.page.element.TourBlockPages(tour).build(page.ElementContentPageBuilder, generator)

  }

}
