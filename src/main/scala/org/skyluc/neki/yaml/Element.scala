package org.skyluc.neki.yaml

import org.virtuslab.yaml.YamlCodec
import org.virtuslab.yaml.Node

trait Element

case class Song(
    id: String,
    fullname: String,
    `fullname-en`: Option[String],
    album: Option[String],
    `release-date`: String,
    credits: Option[Credits],
    `cover-image`: CoverImage,
) extends Element
    derives YamlCodec

case class Album(
    id: String,
    fullname: String,
    altname: Option[String],
    designation: String,
    `release-date`: String,
    `cover-image`: CoverImage,
    songs: List[String],
) extends Element
    derives YamlCodec

case class Credits(
    lyricist: String,
    composer: String,
    source: Option[Source],
) derives YamlCodec

case class CoverImage(
    file: Option[File],
    album: Option[String],
    tour: Option[String],
) derives YamlCodec

case class File(
    filename: String,
    source: Source,
) derives YamlCodec

case class Source(
    description: String,
    url: Option[String],
) derives YamlCodec

case class Site(
    navigation: Navigation
) extends Element
    derives YamlCodec

case class MusicPage(
    id: String,
    music: List[MusicId],
) extends Element
    derives YamlCodec

case class MusicId(
    album: Option[String],
    song: Option[String],
) derives YamlCodec

case class Show(
    year: String,
    id: String,
    fullname: String,
    shortname: Option[String],
    sublabel: Option[String],
    date: String,
    tour: Option[String],
    location: String,
    `event-page`: Option[String],
    setlistfm: Option[String],
    `cover-image`: CoverImage,
) extends Element
    derives YamlCodec

case class ShowsPage(
    id: String,
    shows: List[ShowOrTourId],
) extends Element
    derives YamlCodec

case class ShowOrTourId(
    tour: Option[String],
    show: Option[ShowId],
) derives YamlCodec

case class ShowId(
    year: String,
    id: String,
) derives YamlCodec

case class Tour(
    id: String,
    fullname: String,
    shortname: Option[String],
    `first-date`: String,
    `last-date`: String,
    `event-page`: Option[String],
    `cover-image`: CoverImage,
    shows: List[ShowId],
) extends Element
    derives YamlCodec

case class Navigation(
    main: List[NavigationItem],
    support: List[NavigationItem],
) derives YamlCodec

case class NavigationItem(
    name: String,
    link: String,
) derives YamlCodec
