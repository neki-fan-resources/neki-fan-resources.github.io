package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.data.Datum
import org.skyluc.fan_resources.data.GenId
import org.skyluc.fan_resources.data.Id
import org.skyluc.fan_resources.yaml.FrDecoders
import org.skyluc.fan_resources.yaml.ParserError
import org.skyluc.fan_resources.yaml.YamlKeys.*
import org.skyluc.neki_site.data as d
import org.skyluc.yaml.*

import YamlKeys.*

object NekiSiteDecoders extends FrDecoders {

  override def id: Map[String, YamlDecoder[? <: Id[?], FrDecoders]] = super.id +
    ((PAGE, PageIdDecoder)) +
    ((SITE, SiteIdDecoder))

  override def item: Map[String, YamlDecoder[? <: Datum[?], FrDecoders]] =
    super.item
      + ((CHRONOLOGYPAGE, ChronologyPageDecoder))
      + ((MUSICPAGE, MusicPageDecoder))
      + ((SHOWSPAGE, ShowsPageDecoder))
      + ((SITE, SiteDecoder))
}

case class SiteBuilder(
    navigation: Option[d.Navigation] = None,
    band: Option[d.Band] = None,
    news: Option[List[d.BandNews]] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.Site, SiteBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): SiteBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.Site] = {
    for {
      navigation <- checkDefined(NAVIGATION, navigation)
      band <- checkDefined(BAND, band)
      news <- checkDefined(NEWS, news)
    } yield {
      d.Site(
        navigation,
        band,
        Nil,
        Nil,
        news,
      )
    }
  }
}

object SiteDecoder extends YamlObjectDecoder[d.Site, SiteBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.Site, SiteBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        NAVIGATION,
        NavigationDecoder,
        (b, v) => b.copy(navigation = v),
      ),
      YamlObjectAttribute(
        BAND,
        BandDecoder,
        (b, v) => b.copy(band = v),
      ),
      YamlObjectAttribute(
        NEWS,
        ListDecoder(BandNewsDecoder),
        (b, v) => b.copy(news = v),
      ),
    )

  override def zero(): SiteBuilder = SiteBuilder()

}

object SiteIdDecoder extends FromStringDecoder[GenId[?], FrDecoders] {

  override def fromString(s: String): GenId[?] =
    GenId(SITE, s)

}

case class BandNewsBuilder(
    title: Option[String] = None,
    content: Option[List[String]] = None,
    url: Option[String] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.BandNews, BandNewsBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): BandNewsBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.BandNews] = {
    for {
      title <- checkDefined(TITLE, title)
      content <- checkDefined(CONTENT, content)
      url <- checkDefined(URL, url)
    } yield {
      d.BandNews(
        title,
        content,
        url,
      )
    }
  }
}

object BandNewsDecoder extends YamlObjectDecoder[d.BandNews, BandNewsBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.BandNews, BandNewsBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        TITLE,
        StringDecoder(),
        (b, v) => b.copy(title = v),
      ),
      YamlObjectAttribute(
        CONTENT,
        ListDecoder(StringDecoder()),
        (b, v) => b.copy(content = v),
      ),
      YamlObjectAttribute(
        URL,
        StringDecoder(),
        (b, v) => b.copy(url = v),
      ),
    )

  override def zero(): BandNewsBuilder = BandNewsBuilder()

}

case class BandBuilder(
    members: Option[d.Members] = None,
    socialMedia: Option[d.SocialMedia] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.Band, BandBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): BandBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.Band] =
    for {
      members <- checkDefined(MEMBER, members)
      socialMedia <- checkDefined(SOCIAL_MEDIA, socialMedia)
    } yield {
      d.Band(
        members,
        socialMedia,
      )
    }
}

object BandDecoder extends YamlObjectDecoder[d.Band, BandBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.Band, BandBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        SOCIAL_MEDIA,
        SocialMediaDecoder,
        (b, v) => b.copy(socialMedia = v),
      ),
      YamlObjectAttribute(
        MEMBER,
        MembersDecoder,
        (b, v) => b.copy(members = v),
      ),
    )

  override def zero(): BandBuilder = BandBuilder()

}

case class MembersBuilder(
    cocoro: Option[d.Member] = None,
    hika: Option[d.Member] = None,
    kanade: Option[d.Member] = None,
    natsu: Option[d.Member] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.Members, MembersBuilder, FrDecoders] {

  import ObjectBuilder.*
  import MembersBuilder.*

  override def setErrors(errs: Seq[ParserError]): MembersBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.Members] = {
    for {
      cocoro <- checkDefined(COCORO, cocoro)
      hika <- checkDefined(HIKA, hika)
      kanade <- checkDefined(KANADE, kanade)
      natsu <- checkDefined(NATSU, natsu)
    } yield {
      d.Members(
        cocoro,
        hika,
        kanade,
        natsu,
      )
    }
  }
}

object MembersBuilder {
  val COCORO = "cocoro"
  val HIKA = "hika"
  val KANADE = "kanade"
  val NATSU = "natsu"
}

object MembersDecoder extends YamlObjectDecoder[d.Members, MembersBuilder, FrDecoders] {

  import MembersBuilder.*

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.Members, MembersBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        COCORO,
        MemberDecoder,
        (b, v) => b.copy(cocoro = v),
      ),
      YamlObjectAttribute(
        HIKA,
        MemberDecoder,
        (b, v) => b.copy(hika = v),
      ),
      YamlObjectAttribute(
        KANADE,
        MemberDecoder,
        (b, v) => b.copy(kanade = v),
      ),
      YamlObjectAttribute(
        NATSU,
        MemberDecoder,
        (b, v) => b.copy(natsu = v),
      ),
    )

  override def zero(): MembersBuilder = MembersBuilder()

}

case class MemberBuilder(
    id: Option[String] = None,
    name: Option[String] = None,
    role: Option[String] = None,
    socialMedia: Option[d.SocialMedia] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.Member, MemberBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): MemberBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.Member] = {
    for {
      id <- checkDefined(ID, id)
      name <- checkDefined(NAME, name)
      role <- checkDefined(ROLE, role)
      socialMedia <- checkDefined(SOCIAL_MEDIA, socialMedia)
    } yield {
      d.Member(
        id,
        name,
        role,
        socialMedia,
      )
    }
  }
}

object MemberDecoder extends YamlObjectDecoder[d.Member, MemberBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.Member, MemberBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        ID,
        StringDecoder(),
        (b, v) => b.copy(id = v),
      ),
      YamlObjectAttribute(
        NAME,
        StringDecoder(),
        (b, v) => b.copy(name = v),
      ),
      YamlObjectAttribute(
        ROLE,
        StringDecoder(),
        (b, v) => b.copy(role = v),
      ),
      YamlObjectAttribute(
        SOCIAL_MEDIA,
        SocialMediaDecoder,
        (b, v) => b.copy(socialMedia = v),
      ),
    )

  override def zero(): MemberBuilder = MemberBuilder()

}

case class SocialMediaBuilder(
    instagram: Option[String] = None,
    tiktok: Option[String] = None,
    youtube: Option[String] = None,
    x: Option[String] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.SocialMedia, SocialMediaBuilder, FrDecoders] {

  override def setErrors(errs: Seq[ParserError]): SocialMediaBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.SocialMedia] = {
    Right(
      d.SocialMedia(
        instagram,
        tiktok,
        youtube,
        x,
      )
    )
  }
}

object SocialMediaDecoder extends YamlObjectDecoder[d.SocialMedia, SocialMediaBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.SocialMedia, SocialMediaBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        INSTAGRAM,
        StringDecoder(),
        (b, v) => b.copy(instagram = v),
      ),
      YamlObjectAttribute(
        TIKTOK,
        StringDecoder(),
        (b, v) => b.copy(tiktok = v),
      ),
      YamlObjectAttribute(
        YOUTUBE,
        StringDecoder(),
        (b, v) => b.copy(youtube = v),
      ),
      YamlObjectAttribute(
        X,
        StringDecoder(),
        (b, v) => b.copy(x = v),
      ),
    )

  override def zero(): SocialMediaBuilder = SocialMediaBuilder()

}

case class NavigationBuilder(
    main: Option[Seq[d.NavigationItem]] = None,
    support: Option[Seq[d.NavigationItem]] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.Navigation, NavigationBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): NavigationBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[FrDecoders]): Either[Seq[ParserError], d.Navigation] = {
    for {
      main <- checkDefined(MAIN, main)
      support <- checkDefined(SUPPORT, support)
    } yield {
      d.Navigation(
        main,
        support,
      )
    }
  }
}

object NavigationDecoder extends YamlObjectDecoder[d.Navigation, NavigationBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.Navigation, NavigationBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        MAIN,
        ListDecoder(NavigationItemDecoder),
        (b, v) => b.copy(main = v),
      ),
      YamlObjectAttribute(
        SUPPORT,
        ListDecoder(NavigationItemDecoder),
        (b, v) => b.copy(support = v),
      ),
    )

  override def zero(): NavigationBuilder = NavigationBuilder()

}

case class NavigationItemBuilder(
    name: Option[String] = None,
    link: Option[String] = None,
    highlight: Option[Seq[String]] = None,
    errors: Seq[ParserError] = Seq(),
) extends ObjectBuilder[d.NavigationItem, NavigationItemBuilder, FrDecoders] {

  import ObjectBuilder.*

  override def setErrors(errs: Seq[ParserError]): NavigationItemBuilder = copy(errors = errors :++ errs)

  override protected def build(using
      context: DecoderContext[FrDecoders]
  ): Either[Seq[ParserError], d.NavigationItem] = {
    for {
      name <- checkDefined(NAME, name)
      link <- checkDefined(LINK, link)
      highlight <- checkDefined(HIGHLIGHT, highlight)
    } yield {
      d.NavigationItem(
        name,
        link,
        highlight,
      )
    }
  }
}

object NavigationItemDecoder extends YamlObjectDecoder[d.NavigationItem, NavigationItemBuilder, FrDecoders] {

  override def attributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.NavigationItem, NavigationItemBuilder, FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        NAME,
        StringDecoder(),
        (b, v) => b.copy(name = v),
      ),
      YamlObjectAttribute(
        LINK,
        StringDecoder(),
        (b, v) => b.copy(link = v),
      ),
      YamlObjectAttribute(
        HIGHLIGHT,
        ListDecoder(StringDecoder()),
        (b, v) => b.copy(highlight = v),
      ),
    )

  override def zero(): NavigationItemBuilder = NavigationItemBuilder()

}
