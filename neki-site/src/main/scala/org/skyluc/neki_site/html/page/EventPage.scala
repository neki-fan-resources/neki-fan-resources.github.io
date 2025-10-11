package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object EventPage {
  def pagesFor(event: d.Event, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(event)

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        fr.compileddata.MultimediaCompiledData.getMainContentSections(event, generator),
      )
    )
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(event, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      d.Event.DESIGNATION,
      None,
      event.label,
      None,
      None,
      None,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
