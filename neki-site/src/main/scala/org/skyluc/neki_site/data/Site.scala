package org.skyluc.neki_site.data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.data.{Processor as _, ProcessorWithError as _, WithProcessor as _, *}

case class Site(
    navigation: Navigation,
    band: Band,
    youtubevideo: List[RefMediaIds],
    youtubeshort: List[RefMediaIds],
    news: List[BandNews],
    linkedTo: Seq[Id[?]] = Nil,
    hasError: Boolean = false,
) extends Datum[Site]
    with WithProcessor {
  val id = Site.ID

  override def process[T](processor: Processor[T]): T =
    processor.processSite(this)

  override def process[A](processor: ProcessorWithError[A]): Either[BaseError, A] = {
    processor.processSite(this)
  }

  override def errored() = copy(hasError = true)
  override def withLinkedTo(id: Id[?]*): Site = copy(linkedTo = mergeLinkedTo(id))
}

object Site {
  val ID = GenId[Site]("site", "site")
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
