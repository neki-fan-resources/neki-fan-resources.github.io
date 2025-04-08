package org.skyluc.neki.html.page

import java.nio.file.Path
import org.skyluc.neki.html.Page
import java.nio.file.Files
import org.skyluc.neki.Config

class SitemapPage(val pages: List[Page]) {

  val path = Path.of("sitemap.xml")

  def generate(siteFolder: Path): Unit = {
    val baseUrl = Config.current.baseUrl
    val builder = StringBuilder()
    builder.append("""<?xml version="1.0" encoding="UTF-8"?>""")
    builder.append('\n')
    builder.append("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")
    builder.append('\n')
    pages.filterNot(_.isDark).foreach { p =>
      builder.append("""  <url>""")
      builder.append('\n')
      builder.append(s"""    <loc>${baseUrl}${p.url()}</loc>""")
      builder.append('\n')
      builder.append("""  </url>""")
      builder.append('\n')
    }
    builder.append("""</urlset>""")
    builder.append('\n')

    val filePath = siteFolder.resolve(path)
    Files.createDirectories(filePath.getParent())
    val output = Files.newBufferedWriter(filePath.toAbsolutePath())
    output.write(builder.toString())
    output.close()
  }

}

object SitemapPage {}
