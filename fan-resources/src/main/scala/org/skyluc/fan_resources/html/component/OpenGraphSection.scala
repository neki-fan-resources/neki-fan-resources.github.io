package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.html.PageDescription
import org.skyluc.html.*

import Html.*

object OpenGraphSection {
  def generate(description: PageDescription): Seq[HeadElement[?]] = {
    List(
      link(REL_CANONICAL, description.canonicalUrl.toString()),
      title(description.title),
      meta().withName(NAME_DESCRIPTION).withContent(description.description),
      meta().withProperty(PROPERTY_TITLE).withContent(description.title),
      meta().withProperty(PROPERTY_DESCRIPTION).withContent(description.description),
      meta().withProperty(PROPERTY_IMAGE).withContent(description.image.toString()),
      meta().withProperty(PROPERTY_TYPE).withContent(description.ogType),
      meta().withProperty(PROPERTY_URL).withContent(description.canonicalUrl.toString()),
      meta().withProperty(PROPERTY_LOGO).withContent(description.logo.toString()),
      meta().withProperty(PROPERTY_LOCALE).withContent(description.locale),
    )

  }

  // --------
  private val REL_CANONICAL = "canonical"
  private val NAME_DESCRIPTION = "description"

  private val PROPERTY_TITLE = "og:title"
  private val PROPERTY_DESCRIPTION = "og:description"
  private val PROPERTY_IMAGE = "og:image"
  private val PROPERTY_TYPE = "og:type"
  private val PROPERTY_URL = "og:url"
  private val PROPERTY_LOGO = "og:logo"
  private val PROPERTY_LOCALE = "og:locale"
}
