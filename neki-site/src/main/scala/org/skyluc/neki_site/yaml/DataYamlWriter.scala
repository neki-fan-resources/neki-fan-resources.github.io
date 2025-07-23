package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.yaml as fr
import org.skyluc.neki_site.data as d

import YamlKeys.*
import fr.YamlKeys.*

class DataYamlWriter extends fr.DataYamlWriter with d.Processor[Unit] {

  private def processBand(band: d.Band): Unit = {
    elementStart(BAND)
    processSocialMedia(band.socialMedia)
    elementStart(MEMBER)
    processMember(band.member.hika)
    processMember(band.member.natsu)
    processMember(band.member.kanade)
    processMember(band.member.cocoro)
    elementEnd
    elementEnd
  }

  private def processMember(member: d.Member): Unit = {
    elementStart(member.id)
    attributeString(ID, member.id)
    attributeString(NAME, member.name)
    attributeString(ROLE, member.role)
    processSocialMedia(member.socialMedia)
    elementEnd
  }

  private def processNavigation(navigation: d.Navigation): Unit = {
    elementStart(NAVIGATION)
    elementStart(MAIN, true)
    navigation.main.foreach(processNavigationItem(_))
    elementEnd
    elementStart(SUPPORT, true)
    navigation.support.foreach(processNavigationItem(_))
    elementEnd
    elementEnd
  }

  private def processNavigationItem(navigationItem: d.NavigationItem): Unit = {
    attributeString(NAME, navigationItem.name)
    inList.push(false)
    attributeString(LINK, navigationItem.link)
    attributeListString(HIGHLIGHT, navigationItem.highlight)
    inList.pop()
  }

  private def processNews(news: Seq[d.BandNews]): Unit = {
    elementStart(NEWS, true)
    news.foreach { newsItem =>
      attributeString(TITLE, newsItem.title)
      inList.push(false)
      attributeListString(CONTENT, newsItem.content)
      attributeString(URL, newsItem.url)
      inList.pop()
    }
    elementEnd
  }

  private def processSocialMedia(socialMedia: d.SocialMedia): Unit = {
    elementStart(SOCIAL_MEDIA)
    attributeString(YOUTUBE, socialMedia.youtube)
    attributeString(X, socialMedia.x)
    attributeString(INSTAGRAM, socialMedia.instagram)
    attributeString(TIKTOK, socialMedia.tiktok)
    elementEnd
  }

  override def processSite(site: d.Site): Unit = {
    elementStart(SITE)
    processNews(site.news)
    processBand(site.band)
    processNavigation(site.navigation)
    attributeListStringOpt(YOUTUBESHORT, Nil)
    attributeListStringOpt(YOUTUBEVIDEO, Nil)
    elementEnd
  }

}
