package org.skyluc.neki_site.html

import org.skyluc.collection.Collections
import org.skyluc.html.*
import org.skyluc.neki_site.data.BandNews

import Html.*

object NewsBlock {

  def generate(news: Seq[BandNews]): List[BodyElement[?]] = {

    val dotDivs: Seq[Div] = Collections.applyToHead(
      Range(0, news.size)
        .map(i =>
          div()
            .withId(NEWS_DOT_BASE_ID + i)
            .withClass(CLASS_NEWS_DOT)
            .withOnClick(s"""manualNewsItemSwitch($i)""")
            .appendElements(text("●"))
        )
        .toList
    )(_.withClass(CLASS_NEWS_DOT_SELECTED))

    List(
      div()
        .withClass(CLASS_NEWS)
        .appendElements(
          div().withClass(CLASS_NEWS_TITLE).appendElements(text("Coming up ...")),
          div()
            .withClass(CLASS_NEWS_DOTS)
            .appendElements(dotDivs*),
          div()
            .withClass(CLASS_NEWS_AREA)
            .appendElements(
              BandNewsCard.generate(news)*
            ),
          div().withClass(CLASS_NEWS_TAGS).appendElements(text("#拡散中 #ネキ界隈 #nekikaiwai")),
        )
    )
  }

  // ---------

  val CLASS_NEWS = "news"
  val CLASS_NEWS_TITLE = "news-title"
  val CLASS_NEWS_DOTS = "news-dots"
  val CLASS_NEWS_DOT = "news-dot"
  val CLASS_NEWS_DOT_SELECTED = "news-dot-selected"
  val CLASS_NEWS_AREA = "news-area"
  val CLASS_NEWS_ITEM = "news-item"
  val CLASS_NEWS_ITEM_TITLE = "news-item-title"
  val CLASS_NEWS_ITEM_CONTENT = "news-item-content"
  val CLASS_NEWS_ITEM_HIDDEN = "news-item-hidden"
  val CLASS_NEWS_TAGS = "news-tags"

  val NEWS_ITEM_BASE_ID = "news_item_"
  val NEWS_DOT_BASE_ID = "news_dot_"
}

object BandNewsCard {

  import NewsBlock._

  def generate(bandNews: Seq[BandNews]): Seq[A] = {
    Collections.applyToTail(bandNews.zipWithIndex.map((e, i) => generate(e, i)))(_.withClass(CLASS_NEWS_ITEM_HIDDEN))
  }

  private def generate(bandNews: BandNews, i: Int): A = {
    a()
      .withHref(bandNews.url)
      .withClass(CLASS_NEWS_ITEM)
      .withId(NEWS_ITEM_BASE_ID + i)
      .appendElements(
        div().withClass(CLASS_NEWS_ITEM_TITLE).appendElements(text(bandNews.title)),
        div()
          .withClass(CLASS_NEWS_ITEM_CONTENT)
          .appendElements(
            bandNews.content.map { line =>
              p().appendElements(
                text(line)
              )
            }*
          ),
      )
  }

}
