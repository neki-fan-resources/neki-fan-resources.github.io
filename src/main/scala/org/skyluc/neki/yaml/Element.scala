package org.skyluc.neki.yaml

import org.virtuslab.yaml.YamlCodec
import org.virtuslab.yaml.Node

trait Element

case class Album(
    id: String,
    fullname: String,
    altname: Option[String],
    designation: String,
    description: Option[List[String]],
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

trait WithLocalImageId {
  val media: Option[MediaId]
  val id: String
}

case class LocalImage(
    media: Option[MediaId],
    id: String,
    filename: String,
    label: String,
    `published-date`: String,
) extends Element
    with WithLocalImageId
    derives YamlCodec

case class MediaAudio(
    year: String,
    id: String,
    radio: String,
    show: String,
    designation: String,
    host: String,
    member: List[String],
    program: Option[String],
    webpage: Option[String],
    `published-date`: String,
    description: Option[List[String]],
    `cover-image`: CoverImage,
    summary: Option[Summary],
    multimedia: Option[MultiMedia],
) extends Element
    derives YamlCodec

case class MediaWritten(
    year: String,
    id: String,
    publication: String,
    issue: String,
    designation: String,
    journalist: Option[String],
    member: List[String],
    `article-page`: Option[String],
    webpage: Option[String],
    `published-date`: String,
    description: Option[List[String]],
    `cover-image`: CoverImage,
    summary: Option[Summary],
    multimedia: Option[MultiMedia],
) extends Element
    derives YamlCodec

case class MusicPage(
    id: String,
    music: List[MusicId],
) extends Element
    derives YamlCodec

case class PostX(
    id: String,
    account: String,
    `published-date`: String,
    info: String,
    text: String,
    image: Option[List[PostXImage]],
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
    news: List[NewsItem],
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
    // media
    media: Option[MediaId],
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

case class LocalImageId(
    media: Option[MediaId],
    id: String,
) extends WithLocalImageId
    derives YamlCodec

case class Lyrics(
    status: CriptionLationStatus,
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

case class CriptionLationStatus(
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
    image: Option[List[MultiMediaId]],
    additional: Option[List[MultiMediaId]],
) derives YamlCodec

case class MultiMediaId(
    localImage: Option[LocalImageId],
    postXImage: Option[PostXImageId],
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

case class NewsItem(
    title: String,
    content: List[String],
    // TODO: should be a ref id
    url: String,
) derives YamlCodec

case class PostXImage(
    id: String,
    label: String,
    info: Option[String],
) derives YamlCodec

case class PostXImageId(
    postId: String,
    imageId: String,
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

case class Summary(
    status: CriptionLationStatus,
    items: List[SummaryItemL1],
) derives YamlCodec

trait SummaryItem {
  val label: String
  val sub: Option[List[SummaryItem]]
}

// The Yaml library doesn't like recursive type structure (infinite loop at runtime)

case class SummaryItemL1(
    label: String,
    sub: Option[List[SummaryItemL2]],
) extends SummaryItem
    derives YamlCodec

case class SummaryItemL2(
    label: String,
    sub: Option[List[SummaryItemL3]],
) extends SummaryItem
    derives YamlCodec

case class SummaryItemL3(
    label: String,
    sub: Option[List[SummaryItemL4]],
) extends SummaryItem
    derives YamlCodec

case class SummaryItemL4(
    label: String
) extends SummaryItem
    derives YamlCodec {
  override val sub: Option[List[SummaryItem]] = None
}

case class ZaikoId(
    channel: String,
    id: String,
) derives YamlCodec
