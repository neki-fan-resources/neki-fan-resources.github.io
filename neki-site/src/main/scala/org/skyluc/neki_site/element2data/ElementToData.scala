package org.skyluc.neki_site.element2data

import org.skyluc.fan_resources.BaseError
import org.skyluc.fan_resources.element2data as fr
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.yaml.*

object ElementToData extends fr.ElementToData with Processor[fr.ElementToData.Result] {
  import fr.ElementToData._

  override def processChronologyPage(chronologyPage: ChronologyPage): Either[BaseError, Result] =
    toChronologyPage(chronologyPage).map(d => Result(d, d.chronology.markers))

  override def processMusicPage(musicPage: MusicPage): Either[BaseError, Result] =
    toMusicPage(musicPage).map(d => Result(d))

  override def processShowsPage(showsPage: ShowsPage): Either[BaseError, Result] =
    toShowsPage(showsPage).map(d => Result(d))

  override def processSite(site: Site): Either[BaseError, Result] =
    toSite(site).map(d => Result(d))

  def toBand(band: Band): d.Band = {
    d.Band(
      toMembers(band.member),
      toSocialMedia(band.`social-media`),
    )
  }

  def toChronologyPage(chronologyPage: ChronologyPage): Either[BaseError, d.ChronologyPage] = {
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

  def toMusicPage(musicPage: MusicPage): Either[BaseError, d.MusicPage] = {
    val id = d.PageId(musicPage.id, musicPage.dark)
    for {
      musicIds <- throughList(musicPage.music, id)(processMusicId)
    } yield {
      d.MusicPage(id, musicIds, Nil)
    }
  }

  def toNavigation(navigation: Navigation): Either[BaseError, d.Navigation] = {
    for {
      main <- throughList(navigation.main)(toNavigationItem)
      support <- throughList(navigation.support)(toNavigationItem)
    } yield {
      d.Navigation(main, support)
    }
  }

  def toNavigationItem(navigationItem: NavigationItem): Either[BaseError, d.NavigationItem] = {
    Right(d.NavigationItem(navigationItem.name, navigationItem.link, navigationItem.highlight))
  }

  def toNewsItem(newsItem: NewsItem): d.BandNews = {
    d.BandNews(newsItem.title, newsItem.content, newsItem.url)
  }

  def toShowsPage(showsPage: ShowsPage): Either[BaseError, d.ShowsPage] = {
    val id = d.PageId(showsPage.id)
    for {
      showsIds <- throughList(showsPage.shows, id)(toShowOrTourId)
    } yield {
      d.ShowsPage(id, showsIds, Nil)
    }
  }

  def toSite(site: Site): Either[BaseError, d.Site] = {
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
