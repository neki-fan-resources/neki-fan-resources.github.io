package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object TourPage {
  def pagesFor(tour: d.Tour, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(tour)

    val showsSection = if (tour.shows.isEmpty) {
      Nil
    } else {
      Seq(
        fr.page.ContentSectionChronology(
          "Shows",
          tour.shows.map(generator.getElementCompiledData(_)),
        )
      )
    }

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        showsSection ++ fr.compileddata.MultimediaCompiledData.getMainContentSections(tour, generator),
      )
    )
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(tour, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      d.Tour.DESIGNATION,
      None,
      tour.fullname,
      None,
      tour.shortname,
      None,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
