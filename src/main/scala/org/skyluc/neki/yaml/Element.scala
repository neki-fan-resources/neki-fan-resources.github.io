package org.skyluc.neki.yaml

import org.virtuslab.yaml.YamlCodec

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
    file: Option[File]
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

case class Navigation(
    main: List[NavigationItem],
    support: List[NavigationItem],
) derives YamlCodec

case class NavigationItem(
    name: String,
    link: String,
) derives YamlCodec
