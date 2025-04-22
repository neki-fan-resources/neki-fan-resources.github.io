package org.skyluc.neki_site.html.pages

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.Date
import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.data.Show
import org.skyluc.fan_resources.data.Song
import org.skyluc.fan_resources.data.YouTubeShortId
import org.skyluc.fan_resources.data.YouTubeVideoId
import org.skyluc.fan_resources.html.ElementCompiledData
import org.skyluc.fan_resources.html.MultiMediaCompiledData
import org.skyluc.fan_resources.html.component.LineCard
import org.skyluc.fan_resources.html.component.MainIntro
import org.skyluc.fan_resources.html.component.MultiMediaCard
import org.skyluc.fan_resources.html.component.SectionHeader
import org.skyluc.html.BodyElement
import org.skyluc.neki_site.html.Compilers
import org.skyluc.neki_site.html.PageDescription
import org.skyluc.neki_site.html.SitePage
import org.skyluc.neki_site.html.TitleAndDescription
import org.skyluc.neki_site.html.component.Defaults

class LivePage(content: LivePage.LiveMultiMedia, description: PageDescription, compilers: Compilers)
    extends SitePage(description, compilers) {

  import LivePage._

  override def elementContent(): Seq[BodyElement[?]] = {
    val videoSection: Seq[BodyElement[?]] = content.videos.flatMap { s =>
      Seq(
        SectionHeader.generate(LineCard.generate(s.parent)),
        MultiMediaCard.generateList(s.multimedias, Song.FROM_KEY),
      )
    }

    val concertSection: Seq[BodyElement[?]] = content.concert.flatMap { s =>
      Seq(
        SectionHeader.generate(LineCard.generate(s.parent)),
        MultiMediaCard.generateList(s.multimedias, Song.FROM_KEY),
      )
    }

    val shortSection: Seq[BodyElement[?]] = content.short.flatMap { s =>
      Seq(
        SectionHeader.generate(LineCard.generate(s.parent)),
        MultiMediaCard.generateList(s.multimedias, Song.FROM_KEY),
      )
    }

    Seq(
      MainIntro.generate(MAIN_INTRO_TEXT)
    ) ++ videoSection
      ++ concertSection
      ++ shortSection
  }

}

object LivePage {

  val MAIN_INTRO_TEXT = "Videos of live performances by NEK!."

  val PAGE_PATH = Path("live")

  def pages(compilers: Compilers): Seq[SitePage] = {

    // TODO: use processors

    val songWithLive = compilers.data.all.values.flatMap {
      case s: Song =>
        if (s.multimedia.live.isEmpty) {
          None
        } else {
          Some((s, s.multimedia.live))
        }
      case _ =>
        None
    }

    // TODO: some type info in MultiMedia or MultiMediaId (video, short, image, ....)
    val songWithLiveVideo = songWithLive
      .flatMap { t =>
        val videos = t._2.filter(m => m.isInstanceOf[YouTubeVideoId])
        if (videos.isEmpty) {
          None
        } else {
          Some(
            ElementAndMultiMedias(
              compilers.elementDataCompiler.get(t._1),
              videos.map(compilers.multimediaDataCompiler.get),
            )
          )
        }
      }
      .toSeq
      .sortBy(_.multimedias.map(_.date).max)
      .reverse

    val songWithLiveShort = songWithLive
      .flatMap { t =>
        val videos = t._2.filter(m => m.isInstanceOf[YouTubeShortId])
        if (videos.isEmpty) {
          None
        } else {
          Some(
            ElementAndMultiMedias(
              compilers.elementDataCompiler.get(t._1),
              videos.map(compilers.multimediaDataCompiler.get),
            )
          )
        }
      }
      .toSeq
      .sortBy(_.multimedias.map(_.date).max)
      .reverse

    val showsWithConcerts = compilers.data.all.values
      .flatMap {
        case s: Show =>
          if (s.multimedia.concert.isEmpty) {
            None
          } else {
            Some(
              ElementAndMultiMedias(
                compilers.elementDataCompiler.get(s),
                s.multimedia.concert.map(compilers.multimediaDataCompiler.get),
              )
            )
          }
        case _ =>
          None
      }
      .toSeq
      .sortBy(_.multimedias.map(_.date).max)
      .reverse

    val content = LiveMultiMedia(
      songWithLiveVideo,
      showsWithConcerts,
      songWithLiveShort,
    )

    val mainPage = LivePage(
      content,
      PageDescription(
        TitleAndDescription.formattedTitle(
          None,
          None,
          "Live",
          None,
          None,
          None,
        ),
        TitleAndDescription.formattedDescription(
          None,
          None,
          "Live",
          None,
          None,
          None,
        ),
        SitePage.absoluteUrl(Defaults.COVER_IMAGE.source),
        SitePage.canonicalUrlFor(PAGE_PATH),
        PAGE_PATH.withExtension(Common.HTML_EXTENSION),
        None,
        None,
        false,
      ),
      compilers,
    )

    Seq(mainPage)
  }

  case class LiveMultiMedia(
      videos: Seq[ElementAndMultiMedias],
      concert: Seq[ElementAndMultiMedias],
      short: Seq[ElementAndMultiMedias],
  )

  case class ElementAndMultiMedias(
      parent: ElementCompiledData,
      multimedias: Seq[MultiMediaCompiledData],
  )
}
