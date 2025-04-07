package org.skyluc.neki.html.page

import org.skyluc.neki.data.Song
import java.nio.file.Path
import org.skyluc.neki.data.Data
import org.skyluc.neki.html._
import org.skyluc.html._
import Html._
import org.skyluc.neki.data.MultiMediaId
import org.skyluc.neki.data.Lyrics
import org.skyluc.neki.data.LyricsLanguage
import org.skyluc.neki.data.LyricsLineEntry
import org.skyluc.neki.data.LyricsSection
import org.skyluc.neki.data.LyricsStatus

class SongPage(val song: Song, extraPage: Boolean, data: Data) extends Page(data) {

  import SongPage._

  override val isDark: Boolean = song.id.dark

  override def oppositePage: Option[String] = {
    val otherSongId = song.id.copy(dark = !song.id.dark)
    CompiledData.cache.get(otherSongId).map(_.url)
  }

  override def path(): Path = if (song.id.dark) {
    Path.of(CommonBase.DARK_PATH, SONG_PATH, song.id.id + Pages.HTML_EXTENSION)
  } else {
    Path.of(SONG_PATH, song.id.id + Pages.HTML_EXTENSION)
  }

  override def shortTitle(): String = {
    song.fullname + song.fullnameEn.map(n => s" ($n)").getOrElse(CommonBase.EMPTY) + TITLE_DESIGNATION
  }

  override def altName(): Option[String] = None

  override def mainContent(): List[BodyElement[?]] = {
    val videoSection = MultiMediaCard.generateSection(
      SECTION_VIDEO_TEXT,
      CompiledData.getMultiMedia(song.multimedia.video, data),
      Song.FROM_KEY,
    )

    val liveSection = MultiMediaCard.generateSection(
      SECTION_LIVE_TEXT,
      CompiledData.getMultiMedia(song.multimedia.live, data),
      Song.FROM_KEY,
    )

    val shortSection = MultiMediaCard.generateSection(
      SECTION_SHORT_TEXT,
      CompiledData.getMultiMedia(song.multimedia.short, data),
      Song.FROM_KEY,
    )

    val lyricsSection: List[BodyElement[?]] =
      song.lyrics.map { l => LyricsHtml.PageLyrics.from(l).generate() }.getOrElse(Nil)

    val additionalSection = MultiMediaCard.generateSection(
      SECTION_ADDITIONAL_TEXT,
      CompiledData.getMultiMedia(song.multimedia.additional, data),
      Song.FROM_KEY,
    )

    val extraSection = if (extraPage) {
      List(ExtraLink.generate("/" + extraPath(song).toString()))
    } else {
      Nil
    }

    List(
      ItemDetails.generate(CompiledData.getSong(song.id, data))
    ) ::: videoSection ::: liveSection ::: shortSection ::: lyricsSection ::: additionalSection ::: extraSection
  }

}

object SongPage {
  val SONG_PATH = "song"

  val DESIGNATION = "Song"
  val TITLE_DESIGNATION = " - " + DESIGNATION
  val TITLE_DESIGNATION_EXTRA = " - " + DESIGNATION + " extra"

  val LABEL_LYRICIST = "lyricist"
  val LABEL_COMPOSER = "composer"

  val SECTION_VIDEO_TEXT = "Video"
  val SECTION_LIVE_TEXT = "Live"
  val SECTION_SHORT_TEXT = "Short"
  val SECTION_ADDITIONAL_TEXT = "Additional Media"

  def extraPath(song: Song): Path = Path.of(SONG_PATH, Pages.EXTRA_PATH, song.id.id + Pages.HTML_EXTENSION)

  def extraMultimedia(song: Song): List[MultiMediaId] = {
    val allMultiMediaInMainPage = song.multimedia.all()
    val allRelatedMultiMedia: List[MultiMediaId] = song.relatedTo.flatMap {
      case m: MultiMediaId =>
        Some(m)
      case _ =>
        None
    }
    allRelatedMultiMedia.filterNot(allMultiMediaInMainPage.contains(_))
  }

  def pagesFor(song: Song, data: Data): List[Page] = {
    if (extraMultimedia(song).isEmpty) {
      List(SongPage(song, false, data))
    } else {
      List(SongPage(song, true, data), SongExtraPage(song, data))
    }
  }
}

object LyricsHtml {

  case class PageLyrics(
      status: LyricsStatus,
      languages: List[PageLyricsLanguage],
      sections: List[PageLyricsSection],
  ) {
    def generate(): List[BodyElement[?]] = {
      List(
        SectionHeader.generate(
          text(TEXT_LYRICS_SECTION),
          span()
            .withClass(CLASS_LYRICS_STATUS)
            .withClass(CLASS_LYRICS_STATUS_BASE + status.code)
            .appendElement(
              text(status.description)
            ),
        ),
        table()
          .withClass(CLASS_LYRICS_LANGUAGES_SELECTION)
          .appendTbody(
            tbody().appendTrs(
              languages.map(_.generate())*
            )
          ),
        div()
          .withClass(CLASS_LYRICS_SECTIONS)
          .appendElements(
            sections.map(_.generate())*
          ),
      )
    }
  }

  object PageLyrics {

    def from(lyrics: Lyrics): PageLyrics = {
      PageLyrics(
        lyrics.status,
        lyrics.languages.map(PageLyricsLanguage.from(_)),
        lyrics.sections.map(PageLyricsSection.from(_, lyrics.languages)),
      )
    }
  }

  case class PageLyricsLanguage(
      id: String,
      label: String,
      active: Boolean = false,
      fixed: Boolean = false,
  ) {
    def generate(): Tr = {
      tr()
        .withClass(CLASS_LYRICS_LANGUAGE_SELECTION)
        .appendTds(
          td().appendElements(
            // TODO: add onchange
            inputCheckbox()
              .withId(CLASS_LYRICS_LANG_BASE + id)
              .withChecked(active)
              .withDisabled(fixed)
              .withOnChange(s"""toggleClass('$CLASS_LYRICS_SUB_BASE$id','$CLASS_LYRICS_SUB_HIDDEN')""")
          ),
          td().appendElements(text(label)),
        )
    }
  }

  object PageLyricsLanguage {
    def from(language: LyricsLanguage): PageLyricsLanguage = {
      PageLyricsLanguage(
        language.id,
        language.name + language.details.map(d => " " + d).getOrElse(""),
        language.active,
        language.fixed,
      )
    }
  }

  case class PageLyricsSection(
      lines: List[PageLyricsLine]
  ) {
    def generate(): Div = {
      div()
        .withClass(CLASS_LYRICS_SECTION)
        .appendElements(
          lines.map(_.generate())*
        )
    }
  }

  object PageLyricsSection {
    def from(section: LyricsSection, languages: List[LyricsLanguage]): PageLyricsSection = {
      PageLyricsSection(section.lines.map(PageLyricsLine.from(_, languages)))
    }
  }

  case class PageLyricsLine(
      subs: List[PageLyricsSub]
  ) {
    def generate(): Table = {
      val nbTds = subs.map(_.parts.size).maxOption.getOrElse(0)
      table()
        .withClass(CLASS_LYRICS_LINE)
        .appendTbody(
          tbody().appendTrs(
            subs.map(_.generate(nbTds))*
          )
        )
    }
  }

  object PageLyricsLine {
    def from(subs: Map[String, List[LyricsLineEntry]], languages: List[LyricsLanguage]): PageLyricsLine = {
      PageLyricsLine(
        languages.flatMap { language =>
          subs.get(language.id).map(l => PageLyricsSub.from(l, language))
        }
      )
    }
  }

  case class PageLyricsSub(
      languageId: String,
      notranslation: Boolean,
      defaultHidden: Boolean,
      parts: List[PageLyricsSubPart],
  ) {
    def generate(nbTds: Int): Tr = {
      val element = tr()
        .withClass(CLASS_LYRICS_SUB)
        .withClass(CLASS_LYRICS_SUB_BASE + languageId)
        .appendTds(
          // TODO: bad big O
          parts.init.map(_.generate())
            :+ parts.last
              .generate()
              .withSpan(nbTds - parts.size + 1)
              .withClass(CLASS_LYRICS_SUB_LAST_PART)*
        )

      val element2 = if (notranslation) {
        element.withNoTranslate()
      } else {
        element
      }

      if (defaultHidden) {
        element2.withClass(CLASS_LYRICS_SUB_HIDDEN)
      } else {
        element2
      }
    }
  }

  object PageLyricsSub {
    def from(entries: List[LyricsLineEntry], language: LyricsLanguage): PageLyricsSub = {
      val parts: List[PageLyricsSubPart] = (for {
        urltext <- language.urltext
        baseUrl <- language.baseurl
      } yield {
        entries.map { entry =>
          entry.w
            .map { w =>
              entry.d
                .map { d =>
                  TextAndLink(w, urltext, baseUrl + d)
                }
                .getOrElse {
                  SimpleText(w)
                }
            }
            .getOrElse(
              entry.d
                .map { d =>
                  LinkOnly(urltext, baseUrl + d)
                }
                .getOrElse(SimpleText(CommonBase.EMPTY))
            )
        }
      }).getOrElse {
        entries.flatMap(_.w.map(SimpleText(_)))
      }
      PageLyricsSub(language.id, language.notranslation, !language.active, parts)
    }
  }

  sealed trait PageLyricsSubPart {
    def generate(): Td
  }

  case class SimpleText(t: String) extends PageLyricsSubPart {
    def generate(): Td = {
      td().appendElements(
        text(t)
      )
    }
  }

  case class TextAndLink(t: String, linkText: String, url: String) extends PageLyricsSubPart {
    def generate(): Td = {
      td().appendElements(
        text(t),
        a()
          .withHref(url)
          .withClass(CLASS_LYRICS_SUB_LINK_TINY)
          .withTarget(WW_TARGET)
          .appendElements(
            text(linkText)
          ),
      )
    }
  }

  case class LinkOnly(linkText: String, url: String) extends PageLyricsSubPart {
    def generate(): Td = {
      td().appendElements(
        a()
          .withHref(url)
          .withClass(CLASS_LYRICS_SUB_LINK)
          .withTarget(WW_TARGET)
          .appendElements(
            text(linkText)
          )
      )
    }
  }

  case class empty() extends PageLyricsSubPart {
    def generate(): Td = {
      td()
    }
  }

  // -----

  val TEXT_LYRICS_SECTION = "Lyrics"

  val WW_TARGET = "_blank_ww"

  val CLASS_LYRICS = "lyrics"
  val CLASS_LYRICS_STATUS = "lyrics-status"
  val CLASS_LYRICS_STATUS_BASE = "lyrics-status-"
  val CLASS_LYRICS_LANG_BASE = "lyrics-lang-"
  val CLASS_LYRICS_LANGUAGES_SELECTION = "lyrics-languages-selection"
  val CLASS_LYRICS_LANGUAGE_SELECTION = "lyrics-language-selection"
  val CLASS_LYRICS_SECTIONS = "lyrics-sections"
  val CLASS_LYRICS_SECTION = "lyrics-section"
  val CLASS_LYRICS_LINE = "lyrics-line"
  val CLASS_LYRICS_SUB = "lyrics-sub"
  val CLASS_LYRICS_SUB_HIDDEN = "lyrics-sub-hidden"
  val CLASS_LYRICS_SUB_LINK = "lyrics-sub-link"
  val CLASS_LYRICS_SUB_LINK_TINY = "lyrics-sub-link-tiny"
  val CLASS_LYRICS_SUB_BASE = "lyrics-sub-"
  val CLASS_LYRICS_SUB_LAST_PART = "lyrics-sub-part-last"
}
