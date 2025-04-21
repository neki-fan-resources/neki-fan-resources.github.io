package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.yaml.{Processor => frProcessor, WithProcessor => frWithProcessor, _}
import org.virtuslab.yaml.YamlCodec

trait WithProcessor extends frWithProcessor {

  // TODO: better error
  override def process[E, A](processor: frProcessor[E, A]): Either[E, A] = {
    processor match {
      case p: Processor[E, A] =>
        process(p)
      case _ =>
        throw new NotImplementedError("A processor for neki_site elements is required")
    }
  }

  def process[E, A](processor: Processor[E, A]): Either[E, A]
}

// TODO-NOW: Chronology its own type in FR
case class ChronologyPage(
    id: String,
    `start-date`: String,
    `end-date`: String,
    markers: List[ChronologyMarker],
) extends Element
    with WithProcessor
    derives YamlCodec {

  override def process[E, A](processor: Processor[E, A]): Either[E, A] =
    processor.processChronologyPage(this)
}

case class MusicPage(
    id: String,
    music: List[MusicId],
) extends Element
    with WithProcessor
    derives YamlCodec {

  override def process[E, A](processor: Processor[E, A]): Either[E, A] =
    processor.processMusicPage(this)
}

case class ShowsPage(
    id: String,
    shows: List[ShowOrTourId],
) extends Element
    with WithProcessor
    derives YamlCodec {

  override def process[E, A](processor: Processor[E, A]): Either[E, A] =
    processor.processShowsPage(this)
}

case class Site(
    band: Band,
    navigation: Navigation,
    news: List[NewsItem],
    youtubevideo: List[RefMediaIds],
    youtubeshort: List[RefMediaIds],
) extends Element
    with WithProcessor
    derives YamlCodec {

  override def process[E, A](processor: Processor[E, A]): Either[E, A] =
    processor.processSite(this)
}

// ----------

// TODO: generalize
case class Band(
    member: Members,
    `social-media`: SocialMedia,
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

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
) derives YamlCodec
