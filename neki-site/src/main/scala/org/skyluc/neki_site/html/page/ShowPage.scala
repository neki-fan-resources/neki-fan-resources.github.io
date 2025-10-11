package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.data as d
import org.skyluc.fan_resources.html as fr

object ShowPage {
  def pagesFor(show: d.Show, generator: fr.compileddata.CompiledDataGenerator): Seq[fr.page.ElementContentPage] = {

    val compiledData = generator.getElementCompiledData(show)

    val blocks = Seq(
      fr.page.ContentBlockSections(
        "main",
        "main",
        fr.compileddata.MultimediaCompiledData.getMainContentSections(show, generator),
      )
    )
      ++ fr.page.ContentBlockSections.nonEmpty(
        "extra",
        "extra",
        fr.compileddata.MultimediaCompiledData.getExtraContentSections(show, generator),
      )

    val elementContent = fr.page.ElementContent(
      compiledData,
      blocks,
    )

    val labels = fr.page.Labels(
      compiledData.designation,
      None,
      show.fullname,
      None,
      show.shortname,
      None,
    )

    fr.page.ElementContentPage.pagesFrom(elementContent, ElementContentPageBuilder, labels)
  }
}
