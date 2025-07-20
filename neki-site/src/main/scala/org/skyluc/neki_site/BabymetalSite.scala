package org.skyluc.neki_site

import org.skyluc.neki_site.element2data.ElementToData
import org.skyluc.neki_site.yaml.NekiSiteDecoders
import org.skyluc.neki_site.yaml.NodeToElement
import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.element2data.DataTransformer
import org.skyluc.fan_resources.yaml.ItemDecoder
import org.skyluc.fan_resources.yaml.YamlReader
import org.skyluc.fan_resources.yaml.YamlReaderNew
import org.skyluc.yaml.Parser

import java.nio.file.Path

object BabymetalSite {

  object ParserOld extends Parser[Datum[?]] {

    override def parseFile(file: Path, filename: String): (Seq[BaseError], Seq[Datum[?]]) = {
      val (parserErrors, elementTrees) = YamlReader.load(file, filename, NodeToElement)
      val (toDataErrors, datums) = DataTransformer.toData(elementTrees, ElementToData)
      (parserErrors ++ toDataErrors, datums)
    }

  }

  /* Switching off of old parser */
  object Parser001 extends Parser[Datum[?]] {

    override def parseFile(file: Path, filename: String): (Seq[BaseError], Seq[Datum[?]]) = {
      YamlReaderNew.load(file, filename, ItemDecoder, NekiSiteDecoders)
    }

  }

}
