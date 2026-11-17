package org.skyluc.neki_site.data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.SimpleError
import org.skyluc.fan_resources.base.Path
import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.descriptor.*

import DescriptorProcessor.Context

class SiteId extends fr.Id[Site, SiteId, SiteId.SiteIdBuilder] {

  override val descriptor =
    DescriptorItem(Site.TYPEID, Seq(), () => SiteId.SiteIdBuilder())

  override def get: SiteId = this

  override val path: Path = Path(Site.TYPEID)

}

object SiteId {

  val ID = fr.Id.fixedId[Site](Site.TYPEID)

  class SiteIdBuilder extends fr.Id.IdBuilder[SiteId, SiteIdBuilder] {

    override val errors: Seq[BaseError] = Nil

    override def build()(using context: Context): Either[BaseError, SiteId] = Left(
      SimpleError(context.id, "Not supported")
    )

    override def addErrors(errors: Seq[BaseError]): SiteIdBuilder = this

  }
}

case class Site(
    navigation: Navigation,
    band: Band,
    attributes: fr.Datum.Attributes,
) extends fr.Datum[Site, Site, Site.SiteBuilder]
    with WithProcessor {

  override val id: fr.Id[Site, ?, ?] = SiteId.ID

  override val descriptor = Site.descriptor

  override def get: Site = this

  override def process[T](processor: Processor[T]): T = {
    processor.processSite(this)
  }

}

object Site {

  val TYPEID = Keys.SITE

  case class SiteBuilder(
      navigation: Option[Navigation] = None,
      band: Option[Band] = None,
      attributes: fr.Datum.AttributesBuilderImpl = fr.Datum.AttributesBuilderImpl(),
  ) extends fr.Datum.DatumBuilder[Site, Site, SiteBuilder] {

    override def setAttributes(attributes: fr.Datum.AttributesBuilderImpl) = copy(attributes = attributes)

    override def build()(using context: Context): Either[BaseError, Site] = {
      for {
        navigation <- checkDefined(Keys.NAVIGATION, navigation)
        band <- checkDefined(Keys.BAND, band)
        attributes <- attributes.get()
      } yield {
        Site(
          navigation,
          band,
          attributes,
        )
      }
    }

  }

  val descriptor = new fr.Datum.DescriptorDatum[Site, SiteBuilder] {
    override def attributes: Seq[DescriptorAttribute[Site, SiteBuilder, ?, ?]] =
      Seq[DescriptorAttribute[Site, SiteBuilder, ?, ?]](
        ItemDescriptorAttribute(
          Keys.BAND,
          Band.descriptor,
          (i) => i.band,
          (b, v) => b.copy(band = Some(v)),
        ),
        ItemDescriptorAttribute(
          Keys.NAVIGATION,
          Navigation.descriptor,
          (i) => i.navigation,
          (b, v) => b.copy(navigation = Some(v)),
        ),
      ) ++ attributesDatum

    override val typeId: String = TYPEID

    override val zero = () => SiteBuilder()
  }
}

// =====================

case class Band(
    members: Members,
    socialMedia: SocialMedia,
) extends Item[Band, Band.BandBuilder] {

  override val descriptor = Band.descriptor

  override def get: Band = this

}

object Band {
  val TYPEID = Keys.BAND

  case class BandBuilder(
      members: Option[Members] = None,
      socialMedia: Option[SocialMedia] = None,
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[Band, BandBuilder] {

    override def addErrors(errors: Seq[BaseError]): BandBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, Band] = {
      for {
        members <- checkDefined(fr.Keys.MEMBER, members)
        socialMedia <- checkDefined(Keys.SOCIAL_MEDIA, socialMedia)
      } yield {
        Band(members, socialMedia)
      }
    }

  }

  val descriptor = DescriptorItem[Band, BandBuilder](
    TYPEID,
    Seq(
      ItemDescriptorAttribute(
        Keys.SOCIAL_MEDIA,
        SocialMedia.descriptor,
        (i) => i.socialMedia,
        (b, v) => b.copy(socialMedia = Some(v)),
      ),
      ItemDescriptorAttribute(
        fr.Keys.MEMBER,
        Members.descriptor,
        (i) => i.members,
        (b, v) => b.copy(members = Some(v)),
      ),
    ),
    () => BandBuilder(),
  )
}

case class Member(
    id: String,
    name: String,
    role: String,
    socialMedia: SocialMedia,
) extends Item[Member, Member.MemberBuilder] {

  override val descriptor = Member.descriptor

  override def get: Member = this

}

object Member {
  val TYPEID = fr.Keys.MEMBER

  case class MemberBuilder(
      id: Option[String] = None,
      name: Option[String] = None,
      role: Option[String] = None,
      socialMedia: Option[SocialMedia] = None,
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[Member, MemberBuilder] {

    override def addErrors(errors: Seq[BaseError]): MemberBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, Member] = {
      for {
        id <- checkDefined(fr.Keys.ID, id)
        name <- checkDefined(fr.Keys.NAME, name)
        role <- checkDefined(Keys.ROLE, role)
        socialMedia <- checkDefined(Keys.SOCIAL_MEDIA, socialMedia)
      } yield {
        Member(id, name, role, socialMedia)
      }
    }

  }

  val descriptor = DescriptorItem[Member, MemberBuilder](
    TYPEID,
    Seq(
      StringDescriptorAttribute(
        fr.Keys.ID,
        (i) => i.id,
        (b, v) => b.copy(id = Some(v)),
      ),
      StringDescriptorAttribute(
        fr.Keys.NAME,
        (i) => i.name,
        (b, v) => b.copy(name = Some(v)),
      ),
      StringDescriptorAttribute(
        Keys.ROLE,
        (i) => i.role,
        (b, v) => b.copy(role = Some(v)),
      ),
      ItemDescriptorAttribute(
        Keys.SOCIAL_MEDIA,
        SocialMedia.descriptor,
        (i) => i.socialMedia,
        (b, v) => b.copy(socialMedia = Some(v)),
      ),
    ),
    () => MemberBuilder(),
  )

}

case class Members(
    cocoro: Member,
    hika: Member,
    kanade: Member,
    natsu: Member,
) extends Item[Members, Members.MembersBuilder] {

  override val descriptor = Members.descriptor

  override def get: Members = this

}

object Members {

  val TYPEID = Keys.MEMBERS

  case class MembersBuilder(
      cocoro: Option[Member] = None,
      hika: Option[Member] = None,
      kanade: Option[Member] = None,
      natsu: Option[Member] = None,
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[Members, MembersBuilder] {

    override def addErrors(errors: Seq[BaseError]): MembersBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, Members] = {
      for {
        cocoro <- checkDefined(Keys.COCORO, cocoro)
        hika <- checkDefined(Keys.HIKA, hika)
        kanade <- checkDefined(Keys.KANADE, kanade)
        natsu <- checkDefined(Keys.NATSU, natsu)
      } yield {
        Members(cocoro, hika, kanade, natsu)
      }
    }

  }

  val descriptor = DescriptorItem[Members, MembersBuilder](
    TYPEID,
    Seq(
      ItemDescriptorAttribute(
        Keys.HIKA,
        Member.descriptor,
        (i) => i.hika,
        (b, v) => b.copy(hika = Some(v)),
      ),
      ItemDescriptorAttribute(
        Keys.NATSU,
        Member.descriptor,
        (i) => i.natsu,
        (b, v) => b.copy(natsu = Some(v)),
      ),
      ItemDescriptorAttribute(
        Keys.KANADE,
        Member.descriptor,
        (i) => i.kanade,
        (b, v) => b.copy(kanade = Some(v)),
      ),
      ItemDescriptorAttribute(
        Keys.COCORO,
        Member.descriptor,
        (i) => i.cocoro,
        (b, v) => b.copy(cocoro = Some(v)),
      ),
    ),
    () => MembersBuilder(),
  )

}

case class Navigation(
    main: Seq[NavigationItem],
    support: Seq[NavigationItem],
) extends Item[Navigation, Navigation.NavigationBuilder] {

  override val descriptor = Navigation.descriptor

  override def get: Navigation = this

}

object Navigation {

  val TYPEID = Keys.NAVIGATION

  case class NavigationBuilder(
      main: Seq[NavigationItem] = Seq(),
      support: Seq[NavigationItem] = Seq(),
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[Navigation, NavigationBuilder] {

    override def addErrors(errors: Seq[BaseError]): NavigationBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, Navigation] = {
      Right(Navigation(main, support))
    }

  }

  val descriptor = DescriptorItem[Navigation, NavigationBuilder](
    TYPEID,
    Seq[DescriptorAttribute[Navigation, NavigationBuilder, ?, ?]](
      ListItemDescriptorAttribute(
        fr.Keys.MAIN,
        () => NavigationItem.descriptor,
        (i) => i.main,
        (b, v) => b.copy(main = v),
      ),
      ListItemDescriptorAttribute(
        Keys.SUPPORT,
        () => NavigationItem.descriptor,
        (i) => i.support,
        (b, v) => b.copy(support = v),
      ),
    ),
    () => NavigationBuilder(),
  )

}

case class NavigationItem(
    name: String,
    link: String,
    highlight: Seq[String],
) extends Item[NavigationItem, NavigationItem.NavigationItemBuilder] {

  override val descriptor = NavigationItem.descriptor

  override def get: NavigationItem = this

}

object NavigationItem {

  val TYPEID = Keys.NAVIGATIONITEM

  case class NavigationItemBuilder(
      name: Option[String] = None,
      link: Option[String] = None,
      highlight: Seq[String] = Seq(),
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[NavigationItem, NavigationItem.NavigationItemBuilder] {

    override def addErrors(errors: Seq[BaseError]): NavigationItemBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, NavigationItem] = {
      for {
        name <- checkDefined(fr.Keys.NAME, name)
        link <- checkDefined(Keys.LINK, link)
      } yield {
        NavigationItem(
          name,
          link,
          highlight,
        )
      }

    }
  }

  val descriptor = DescriptorItem[NavigationItem, NavigationItemBuilder](
    TYPEID,
    Seq(
      StringDescriptorAttribute(
        fr.Keys.NAME,
        (i) => i.name,
        (b, v) => b.copy(name = Some(v)),
      ),
      StringDescriptorAttribute(
        Keys.LINK,
        (i) => i.link,
        (b, v) => b.copy(link = Some(v)),
      ),
      ListStringDescriptorAttribute(
        Keys.HIGHLIGHT,
        (i) => i.highlight,
        (b, v) => b.copy(highlight = v),
      ),
    ),
    () => NavigationItemBuilder(),
  )
}

case class SocialMedia(
    instagram: Option[String],
    tiktok: Option[String],
    youtube: Option[String],
    x: Option[String],
) extends Item[SocialMedia, SocialMedia.SocialMediaBuilder] {

  override val descriptor = SocialMedia.descriptor

  override def get: SocialMedia = this

}

object SocialMedia {
  val TYPEID = Keys.SOCIALMEDIA

  case class SocialMediaBuilder(
      instagram: Option[String] = None,
      tiktok: Option[String] = None,
      youtube: Option[String] = None,
      x: Option[String] = None,
      errors: Seq[BaseError] = Seq(),
  ) extends Builder[SocialMedia, SocialMedia.SocialMediaBuilder] {

    override def addErrors(errors: Seq[BaseError]): SocialMediaBuilder = copy(errors = this.errors ++ errors)

    override def build()(using context: Context): Either[BaseError, SocialMedia] = {
      Right(SocialMedia(instagram, tiktok, youtube, x))
    }

  }

  val descriptor = DescriptorItem[SocialMedia, SocialMediaBuilder](
    TYPEID,
    Seq(
      OptionStringDescriptorAttribute(
        Keys.YOUTUBE,
        (i) => i.youtube,
        (b, v) => b.copy(youtube = Some(v)),
      ),
      OptionStringDescriptorAttribute(
        Keys.X,
        (i) => i.x,
        (b, v) => b.copy(x = Some(v)),
      ),
      OptionStringDescriptorAttribute(
        Keys.INSTAGRAM,
        (i) => i.instagram,
        (b, v) => b.copy(instagram = Some(v)),
      ),
      OptionStringDescriptorAttribute(
        Keys.TIKTOK,
        (i) => i.tiktok,
        (b, v) => b.copy(tiktok = Some(v)),
      ),
    ),
    () => SocialMediaBuilder(),
  )

}
