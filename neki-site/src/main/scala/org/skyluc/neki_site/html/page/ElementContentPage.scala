package org.skyluc.neki_site.html.page

import org.skyluc.fan_resources.html as fr

import fr.page.ElementContent

object ElementContentPageBuilder extends fr.page.ElementContentPageBuilder {
  override def build(elementContent: ElementContent, index: Int, labels: fr.page.Labels): fr.page.ElementContentPage = {
    ElementContentPage(elementContent, index, labels)
  }
}

class ElementContentPage(
    override val elementContent: ElementContent,
    override val index: Int,
    labels: fr.page.Labels,
) // TODO: move up to FR
    extends MainSitePage
    with fr.page.ElementContentPage {

  override val pageConfiguration: fr.page.MainSitePageConfiguration = {
    val title = TitleGenerator.generateTitle(labels)
    val description = TitleGenerator.generateDescription(labels)
    MainSitePageConfiguration(
      title,
      description,
      outputPath.toAbsoluteString(),
      elementContent.compiledData.cover.imageUrl,
      false,
    )
  }

}
