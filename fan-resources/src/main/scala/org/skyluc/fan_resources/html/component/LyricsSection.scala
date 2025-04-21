package org.skyluc.fan_resources.html.component

import org.skyluc.fan_resources.Common
import org.skyluc.fan_resources.data.*
import org.skyluc.html.*

import Html.*

object LyricsSection {

  private val TEXT_LYRICS_SECTION = "Lyrics"

  private val WW_TARGET = "_blank_ww"

  private val CLASS_LYRICS_LANG_BASE = "lyrics-lang-"
  private val CLASS_LYRICS_LANGUAGES_SELECTION = "lyrics-languages-selection"
  private val CLASS_LYRICS_LANGUAGE_SELECTION = "lyrics-language-selection"
  private val CLASS_LYRICS_SECTIONS = "lyrics-sections"
  private val CLASS_LYRICS_SECTION = "lyrics-section"
  private val CLASS_LYRICS_LINE = "lyrics-line"
  private val CLASS_LYRICS_SUB = "lyrics-sub"
  private val CLASS_LYRICS_SUB_HIDDEN = "lyrics-sub-hidden"
  private val CLASS_LYRICS_SUB_LINK = "lyrics-sub-link"
  private val CLASS_LYRICS_SUB_LINK_TINY = "lyrics-sub-link-tiny"
  private val CLASS_LYRICS_SUB_BASE = "lyrics-sub-"
  private val CLASS_LYRICS_SUB_LAST_PART = "lyrics-sub-part-last"

  def generate(lyrics: Lyrics): Seq[BodyElement[?]] = {
    PageLyrics.from(lyrics).generate()
  }

  case class PageLyrics(
      status: CriptionLationStatus,
      languages: List[PageLyricsLanguage],
      sections: List[PageLyricsSection],
  ) {
    def generate(): List[BodyElement[?]] = {
      List(
        SectionHeader.generateWithStatus(TEXT_LYRICS_SECTION, status.description, status.code),
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
                .getOrElse(SimpleText(Common.EMPTY))
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

}
