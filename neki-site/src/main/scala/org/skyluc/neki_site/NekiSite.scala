package org.skyluc.neki_site

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.yaml.ItemDecoder
import org.skyluc.fan_resources.yaml.YamlReader
import org.skyluc.neki_site.yaml.NekiSiteDecoders
import org.skyluc.yaml.Parser

object NekiSite {

  /* Switching off of old parser */
  object Parser001 extends Parser[Datum[?]] {

    override def parseFile(file: Path, filename: String): (Seq[BaseError], Seq[Datum[?]]) = {
      YamlReader.load(file, filename, ItemDecoder, NekiSiteDecoders)
    }

  }

}
