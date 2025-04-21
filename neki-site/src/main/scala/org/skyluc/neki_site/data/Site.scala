package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data.{Processor as _, ProcessorWithError as _, WithProcessor as _, *}

case class Site(
    navigation: Navigation,
    band: Band,
    youtubevideo: List[RefMediaIds],
    youtubeshort: List[RefMediaIds],
    news: List[BandNews],
    hasError: Boolean = false,
) extends Datum[Site]
    with WithProcessor {
  val id = Site.ID
  val linkedTo: List[Id[?]] = Nil

  override def process[T](processor: Processor[T]): T =
    processor.processSite(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processSite(this)
  }

  override def errored() = copy(hasError = true)
  override def withLinkedTo(id: Id[?]*): Site = ???
}

object Site {
  val ID = new Id[Site] {
    val path = Path("site")
  }
}

// -----------

case class Band(
    member: Members,
    socialMedia: SocialMedia,
)

case class BandNews(
    title: String,
    content: List[String],
    url: String,
)

case class Member(
    id: String,
    name: String,
    role: String,
    socialMedia: SocialMedia,
)

case class Members(
    cocoro: Member,
    hika: Member,
    kanade: Member,
    natsu: Member,
)

case class Navigation(
    main: List[NavigationItem],
    support: List[NavigationItem],
)

case class NavigationItem(
    name: String,
    link: String,
    highlight: List[String],
)

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
)
