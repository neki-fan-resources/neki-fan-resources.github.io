package org.skyluc.neki.data

import java.nio.file.Path

case class Site(
    navigation: Navigation,
    error: Boolean = false,
) extends Item[Site] {
  val id = Site.ID

  override def errored() = copy(error = true)
}

object Site {
  val ID = new Id {
    val uid = "site"
    val upath = "site/"
    val path = Path.of(uid)
  }
}

case class Navigation(
    main: List[NavigationItem],
    support: List[NavigationItem],
)

case class NavigationItem(
    name: String,
    link: String,
)
