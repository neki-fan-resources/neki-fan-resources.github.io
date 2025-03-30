package org.skyluc.neki.data

case class Site(
  navigation: Navigation,
) extends Item

case class Navigation(
  main: List[NavigationItem],
  support: List[NavigationItem],
)

case class NavigationItem(
  name: String,
  link: String,
)