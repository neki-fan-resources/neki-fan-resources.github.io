package org.skyluc.neki.html.page

import org.skyluc.neki.data.Data
import org.skyluc.neki.html.Page
import org.skyluc.html._
import java.nio.file.Path
import org.skyluc.neki.html.Pages
import org.skyluc.neki.html.MainIntro
import org.skyluc.neki.html.SectionHeader
import org.skyluc.neki.html.LineCard
import org.skyluc.neki.html.CompiledData
import org.skyluc.neki.html.MultiMediaCard
import org.skyluc.neki.data.Date
import org.skyluc.neki.data.Song
import org.skyluc.neki.data.SongId
import org.skyluc.neki.data.YouTubeVideoId
import org.skyluc.neki.data.YouTubeShortId
import org.skyluc.neki.html.MultiMediaCompiledData
import org.skyluc.neki.data.Show

class LivePage(data: Data) extends Page(data) {

  import LivePage._

  override def path(): Path = Path.of(LIVE_PATH)

  override def shortTitle(): String = DESIGNATION

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {

    val songs =
      data.songs.values
        .map(SongWithVideos.from(_, data))
        .toList

    val songWithVideos = songs
      .filterNot(_.youtubeVideos.isEmpty)
      .sortBy { s =>
        s.youtubeVideos.map(_.date).max
      }
      .reverse

    val songWithShorts = songs
      .filterNot(_.youtubeShorts.isEmpty)
      .sortBy { s =>
        s.youtubeShorts.map(_.date).max
      }
      .reverse

    val videoSection: List[BodyElement[?]] = songWithVideos.flatMap { song =>
      List(
        SectionHeader.generate(LineCard.generate(CompiledData.getSong(song.song, data))),
        MultiMediaCard.generateList(song.youtubeVideos, Song.FROM_KEY),
      )
    }

    val shortSection: List[BodyElement[?]] = songWithShorts.flatMap { song =>
      List(
        SectionHeader.generate(LineCard.generate(CompiledData.getSong(song.song, data))),
        MultiMediaCard.generateList(song.youtubeShorts, Song.FROM_KEY),
      )
    }

    val showsWithConcerts = data.shows.values
      .filterNot(_.multimedia.concert.isEmpty)
      .toList
      .sortBy { show =>
        show.multimedia.concert.map(data.multimedia(_).publishedDate).max
      }
      .reverse

    val concertSection: List[BodyElement[?]] = showsWithConcerts.flatMap { show =>
      List(
        SectionHeader.generate(LineCard.generate(CompiledData.getShow(show.id, data))),
        MultiMediaCard.generateList(CompiledData.getMultiMedia(show.multimedia.concert, data), Show.FROM_KEY),
      )
    }

    List(
      MainIntro.generate(MAIN_INTRO_TEXT)
    ) ::: videoSection ::: concertSection ::: shortSection
  }

}

object LivePage {

  case class SongWithVideos(
      song: SongId,
      youtubeVideos: List[MultiMediaCompiledData],
      youtubeShorts: List[MultiMediaCompiledData],
  )

  object SongWithVideos {
    def from(song: Song, data: Data): SongWithVideos = {
      song.multimedia.live.foldLeft(SongWithVideos(song.id, Nil, Nil)) { (acc, multimedia) =>
        multimedia match {
          case y: YouTubeVideoId =>
            acc.copy(youtubeVideos = acc.youtubeVideos :+ CompiledData.getMultiMedia(y, data))
          case y: YouTubeShortId =>
            acc.copy(youtubeShorts = acc.youtubeShorts :+ CompiledData.getMultiMedia(y, data))
          case _ =>
            acc
        }
      }
    }
  }

  val LIVE_PATH = "live" + Pages.HTML_EXTENSION
  val DESIGNATION = "Live"

  val MAIN_INTRO_TEXT = "Videos of live performances by NEK!."
}
