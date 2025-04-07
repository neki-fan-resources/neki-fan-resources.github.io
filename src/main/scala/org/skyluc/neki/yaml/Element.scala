package org.skyluc.neki.yaml

import org.virtuslab.yaml.YamlCodec
import org.virtuslab.yaml.Node

trait Element

case class Album(
    id: String,
    fullname: String,
    altname: Option[String],
    designation: String,
    `release-date`: String,
    `cover-image`: CoverImage,
    songs: List[String],
    multimedia: Option[MultiMedia],
) extends Element
    derives YamlCodec

case class ChronologyPage(
    id: String,
    `start-date`: String,
    `end-date`: String,
    markers: List[ChronologyMarker],
) extends Element
    derives YamlCodec

case class Media(
    year: String,
    id: String,
    radio: String,
    show: String,
    program: Option[String],
    host: String,
    member: List[String],
    webpage: Option[String],
    `published-date`: String,
    `cover-image`: CoverImage,
) extends Element
    derives YamlCodec

case class MusicPage(
    id: String,
    music: List[MusicId],
) extends Element
    derives YamlCodec

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
    multimedia: Option[MultiMedia],
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

case class Site(
    band: Band,
    navigation: Navigation,
    youtubevideo: List[RefMediaIds],
    youtubeshort: List[RefMediaIds],
) extends Element
    derives YamlCodec

case class Song(
    id: String,
    dark: Boolean = false,
    fullname: String,
    `fullname-en`: Option[String],
    album: Option[String],
    `release-date`: String,
    credits: Option[Credits],
    `cover-image`: CoverImage,
    multimedia: Option[MultiMedia],
    lyrics: Option[Lyrics],
) extends Element
    derives YamlCodec

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

case class YouTubeShort(
    id: String,
    label: String,
    info: Option[String],
    `published-date`: String,
    `related-to`: Option[List[Id]],
) extends Element
    derives YamlCodec

case class YouTubeVideo(
    id: String,
    label: String,
    `published-date`: String,
    `related-to`: Option[List[Id]],
) extends Element
    derives YamlCodec

case class Zaiko(
    channel: String,
    id: String,
    label: String,
    `cover-image`: String,
    `published-date`: String,
    `expiration-date`: Option[String],
    `related-to`: Option[List[Id]],
) extends Element
    derives YamlCodec

// ---------

case class Band(
    member: Members,
    `social-media`: SocialMedia,
) derives YamlCodec

case class ChronologyMarker(
    // base marker
    marker: Option[String],
    date: Option[String],
    image: Option[String],
    // show
    show: Option[ShowId],
    short: Boolean = false,
    // song:
    song: Option[String],
    // album:
    album: Option[String],
    // youtubevideo
    youtubevideo: Option[String],
    // interview
    interview: Option[MediaId],
    // common
    `parent-key`: Option[String],
    `related-multimedia`: Option[MultiMediaId],
    up: Int = 0,
    in: Int = 0,
) derives YamlCodec

case class Credits(
    lyricist: String,
    composer: String,
    source: Option[Source],
) derives YamlCodec

case class CoverImage(
    file: Option[File],
    song: Option[String],
    album: Option[String],
    tour: Option[String],
) derives YamlCodec

case class File(
    filename: String,
    source: Source,
) derives YamlCodec

case class Id(
    song: Option[String],
    show: Option[ShowId],
) derives YamlCodec

case class Lyrics(
    status: LyricsStatus,
    languages: List[LyricsLanguage],
    sections: List[LyricsSection],
) derives YamlCodec

case class LyricsLanguage(
    id: String,
    name: String,
    details: Option[String],
    baseurl: Option[String],
    urltext: Option[String],
    active: Boolean = false,
    fixed: Boolean = false,
    notranslation: Boolean = false,
    source: Option[Source],
) derives YamlCodec

case class LyricsLine(
    ol: Option[List[String]],
    oll: Option[String],
    tr: Option[List[String]],
    trl: Option[String],
    atr: Option[List[String]],
    ro: Option[List[String]],
    aro: Option[List[String]],
    ww: Option[List[LyricsWW]],
    gg: Option[String],
    en: Option[String],
) derives YamlCodec

case class LyricsWW(
    w: Option[String],
    d: Option[String],
) derives YamlCodec

case class LyricsSection(
    lines: List[LyricsLine]
) derives YamlCodec

case class LyricsStatus(
    code: String,
    description: String,
) derives YamlCodec

case class MediaId(
    year: String,
    id: String,
) derives YamlCodec

case class RefMediaIds(
    account: String,
    ids: List[String],
) derives YamlCodec

case class Member(
    id: String,
    name: String,
    role: String,
    `social-media`: SocialMedia,
) derives YamlCodec

case class Members(
    kanade: Member,
    hika: Member,
    cocoro: Member,
    natsu: Member,
) derives YamlCodec

case class MultiMedia(
    video: Option[List[MultiMediaId]],
    concert: Option[List[MultiMediaId]],
    live: Option[List[MultiMediaId]],
    short: Option[List[MultiMediaId]],
    additional: Option[List[MultiMediaId]],
) derives YamlCodec

case class MultiMediaId(
    youtubevideo: Option[String],
    youtubeshort: Option[String],
    zaiko: Option[ZaikoId],
) derives YamlCodec

case class MusicId(
    album: Option[String],
    song: Option[String],
) derives YamlCodec

case class Navigation(
    main: List[NavigationItem],
    support: List[NavigationItem],
) derives YamlCodec

case class NavigationItem(
    name: String,
    link: String,
    highlight: List[String],
) derives YamlCodec

case class ShowId(
    year: String,
    id: String,
) derives YamlCodec

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
) derives YamlCodec

case class Source(
    description: String,
    url: Option[String],
) derives YamlCodec

case class ZaikoId(
    channel: String,
    id: String,
) derives YamlCodec
