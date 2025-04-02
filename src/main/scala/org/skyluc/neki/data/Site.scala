package org.skyluc.neki.data

import java.nio.file.Path

case class Site(
    navigation: Navigation,
    band: Band,
    error: Boolean = false,
) extends Item[Site] {
  val id = Site.ID

  override def errored() = copy(error = true)
  override def withRelatedTo(id: Id[?]): Site = ???
}

object Site {
  val ID = new Id[Site] {
    val uid = "site"
    val upath = "site/"
    val path = Path.of(uid)

    def isKnown(sourceId: Id[?], data: Data) = None
  }
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
    main: List[NavigationItem],
    support: List[NavigationItem],
)

case class NavigationItem(
    name: String,
    link: String,
)

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
)
