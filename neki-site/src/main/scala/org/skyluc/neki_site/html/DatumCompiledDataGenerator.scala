package org.skyluc.neki_site.html

import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.Id
import org.skyluc.fan_resources.html as fr
import org.skyluc.neki_site.data.ChronologyPage
import org.skyluc.neki_site.data.MusicPage
import org.skyluc.neki_site.data.Processor
import org.skyluc.neki_site.data.ShowsPage

import fr.CompiledDataGenerator
import fr.DatumCompiledData
import fr.CompiledDataAttributeText
import org.skyluc.fan_resources.data.Path

class DatumCompiledDataGenerator(datums: Seq[Datum[?]], backupPath: Path, generator: CompiledDataGenerator)
    extends fr.DatumCompiledDataGenerator(datums, backupPath, generator)
    with Processor[DatumCompiledData] {

  override def missingCompiledData(id: Id[?]): DatumCompiledData = DatumCompiledData(
    id,
    Site.MISSING_IMAGE,
  )

  override def processChronologyPage(chronologyPage: ChronologyPage): DatumCompiledData = {
    val attributes = Seq(
      CompiledDataAttributeText("startDate", chronologyPage.chronology.startDate.toString()),
      CompiledDataAttributeText("endDate", chronologyPage.chronology.endDate.toString()),
    )

    DatumCompiledData(
      chronologyPage.id,
      "Chronology Page",
      Site.MISSING_IMAGE,
      attributes,
      chronologyPage.linkedTo,
    )
  }

  override def processMusicPage(musicPage: MusicPage): DatumCompiledData = {
    val attributes = attributesFor("song/album", musicPage.music)

    DatumCompiledData(
      musicPage.id,
      "Music Page",
      generator.missingElement.cover,
      attributes,
      musicPage.linkedTo,
    )
  }

  override def processSite(site: org.skyluc.neki_site.data.Site): DatumCompiledData = {
    val attributes = Seq()

    DatumCompiledData(
      site.id,
      "Site",
      generator.missingElement.cover,
      attributes,
      site.linkedTo,
    )
  }

  override def processShowsPage(showsPage: ShowsPage): DatumCompiledData = {
    val attributes = attributesFor("show/tour", showsPage.shows)

    DatumCompiledData(
      showsPage.id,
      "Shows Page",
      generator.missingElement.cover,
      attributes,
      showsPage.linkedTo,
    )
  }

}
