package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.Config

import fr.Url

case class PageDescription(
    title: String,
    description: String,
    image: Url,
    canonicalUrl: Url,
    outputPath: Path,
    oppositePage: Option[Url],
    extraPage: Option[Url],
    isDark: Boolean,
    isRoot: Boolean = false,
) extends fr.PageDescription {

  import PageDescription._

  val ogType: String = VALUE_TYPE
  val logo: Url = Url(Config.current.baseUrl, "manekineko-512px.png")
  val locale: String = VALUE_LOCALE
}

object PageDescription {
  val VALUE_TYPE = "website"
  val VALUE_LOCALE = "en_US"
}
