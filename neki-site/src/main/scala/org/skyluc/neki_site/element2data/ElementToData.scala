package org.skyluc.neki_site.element2data

import org.skyluc.neki_site.yaml._

import org.skyluc.fan_resources.{element2data => fr}
import org.skyluc.fan_resources.data.Datum
import fr.DataTransformer.ToDataError

import org.skyluc.neki_site.{data => d}

object ElementToData extends fr.ElementToData with Processor[ToDataError, Datum[?]] {

  override def processChronologyPage(chronologyPage: ChronologyPage): Either[ToDataError, Datum[?]] =
    toChronologyPage(chronologyPage)

  override def processMusicPage(musicPage: MusicPage): Either[ToDataError, Datum[?]] =
    toMusicPage(musicPage)

  override def processShowsPage(showsPage: ShowsPage): Either[ToDataError, Datum[?]] =
    toShowsPage(showsPage)

  override def processSite(site: Site): Either[ToDataError, Datum[?]] =
    toSite(site)

  def toBand(band: Band): d.Band = {
    d.Band(
      toMembers(band.member),
      toSocialMedia(band.`social-media`),
    )
  }

  def toChronologyPage(chronologyPage: ChronologyPage): Either[ToDataError, d.ChronologyPage] = {
    val id = d.PageId(chronologyPage.id)
    for {
      startDate <- toDate(chronologyPage.`start-date`, id)
      endDate <- toDate(chronologyPage.`end-date`, id)
      markers <- throughList(chronologyPage.markers, id)(toChronologyMarker)
    } yield {
      d.ChronologyPage(
        id,
        org.skyluc.fan_resources.data.Chronology(markers, startDate, endDate),
      )
    }
  }

  def toMember(member: Member): d.Member = {
    d.Member(
      member.id,
      member.name,
      member.role,
      toSocialMedia(member.`social-media`),
    )
  }

  def toMembers(member: Members): d.Members = {
    d.Members(
      toMember(member.cocoro),
      toMember(member.hika),
      toMember(member.kanade),
      toMember(member.natsu),
    )
  }

  def toMusicPage(musicPage: MusicPage): Either[ToDataError, d.MusicPage] = {
    val id = d.PageId(musicPage.id)
    for {
      musicIds <- throughList(musicPage.music, id)(processMusicId)
    } yield {
      d.MusicPage(id, musicIds)
    }
  }

  def toNavigation(navigation: Navigation): Either[ToDataError, d.Navigation] = {
    for {
      main <- throughList(navigation.main)(toNavigationItem)
      support <- throughList(navigation.support)(toNavigationItem)
    } yield {
      d.Navigation(main, support)
    }
  }

  def toNavigationItem(navigationItem: NavigationItem): Either[ToDataError, d.NavigationItem] = {
    Right(d.NavigationItem(navigationItem.name, navigationItem.link, navigationItem.highlight))
  }

  def toNewsItem(newsItem: NewsItem): d.BandNews = {
    d.BandNews(newsItem.title, newsItem.content, newsItem.url)
  }

  def toShowsPage(showsPage: ShowsPage): Either[ToDataError, d.ShowsPage] = {
    val id = d.PageId(showsPage.id)
    for {
      showsIds <- throughList(showsPage.shows, id)(toShowOrTourId)
    } yield {
      d.ShowsPage(id, showsIds)
    }
  }

  def toSite(site: Site): Either[ToDataError, d.Site] = {
    val band = toBand(site.band)
    for {
      navigation <- toNavigation(site.navigation)
    } yield {
      d.Site(
        navigation,
        band,
        site.youtubevideo.map(toMediaIds),
        site.youtubeshort.map(toMediaIds),
        site.news.map(toNewsItem),
      )
    }
  }

  def toSocialMedia(socialMedia: SocialMedia): d.SocialMedia = {
    d.SocialMedia(
      socialMedia.instagram,
      socialMedia.tiktok,
      socialMedia.youtube,
      socialMedia.x,
    )
  }

}
