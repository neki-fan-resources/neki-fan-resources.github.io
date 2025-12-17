package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data.{Processor as _, WithProcessor as _, *}

case class Site(
    attributes: DatumAttributes,
    navigation: Navigation,
    band: Band,
    youtubevideo: List[RefMediaIds],
    youtubeshort: List[RefMediaIds],
) extends Datum[Site]
    with WithProcessor {
  val id = Site.ID

  override def process[T](processor: Processor[T]): T =
    processor.processSite(this)
}

object Site {
  val ID = GenId[Site]("site", "site")
}

// -----------

case class Band(
    member: Members,
    socialMedia: SocialMedia,
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
    main: Seq[NavigationItem],
    support: Seq[NavigationItem],
)

case class NavigationItem(
    name: String,
    link: String,
    highlight: Seq[String],
)

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
)
