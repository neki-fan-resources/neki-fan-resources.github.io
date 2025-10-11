package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object MediaPage {
  def pagesFor(media: d.Media[?], generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(media)

    val summarySection = media.summary.map { summary =>
      fr.page.ContentSectionSummary("Summary", summary)
    }.toSeq

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        summarySection ++ fr.compileddata.MultimediaCompiledData.getMainContentSections(media, generator),
      )
    )
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(media, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      compiledData.designation,
      None,
      media.title(),
      None,
      None,
      None,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
