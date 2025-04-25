package org.skyluc.fan_resources.html.pages

import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.html.HtmlPage
import org.skyluc.fan_resources.html.Page

class SitemapPage(val pages: Seq[HtmlPage]) extends Page {

  override val outputPath: Path = Path("sitemap.xml")

  def content(): String = {
    val builder = StringBuilder()
    builder.append("""<?xml version="1.0" encoding="UTF-8"?>""")
    builder.append('\n')
    builder.append("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")
    builder.append('\n')
    pages.filterNot(_.description.outputPath.firstSegment() == "dark").foreach { p =>
      builder.append("""  <url>""")
      builder.append('\n')
      builder.append(s"""    <loc>${p.description.canonicalUrl}</loc>""")
      builder.append('\n')
      builder.append("""  </url>""")
      builder.append('\n')
    }
    builder.append("""</urlset>""")
    builder.append('\n')

    builder.toString()
  }

}

object SitemapPage {}
