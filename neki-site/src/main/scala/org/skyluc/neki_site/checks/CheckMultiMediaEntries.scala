package org.skyluc.neki_site.checks

import org.skyluc.fan_resources.data.YouTubeShortId
import org.skyluc.fan_resources.data.YouTubeVideoId
import org.skyluc.neki_site.data.Data

object CheckMultiMediaEntries {

  def check(data: Data): List[CheckError] = {

    // TODO: using a processor feel a bit overkill, but maybe ?

    val definedYouTubeShorts = data.all.keys.flatMap {
      case y: YouTubeShortId =>
        Some(y.id)
      case _ =>
        None
    }.toSeq

    val listedYoutubeShorts = data.site.youtubeshort.flatMap(_.ids)

    val shortListedButNotDefined =
      listedYoutubeShorts.diff(definedYouTubeShorts).map(y => CheckError(YouTubeShortId(y), "listed but not defined"))
    val shortDefinedButNotListed =
      definedYouTubeShorts.diff(listedYoutubeShorts).map(y => CheckError(YouTubeShortId(y), "defined but not listed"))

    val definedYouTubeVideos = data.all.keys.flatMap {
      case y: YouTubeVideoId =>
        Some(y.id)
      case _ =>
        None
    }.toSeq

    val listedYoutubeVideos = data.site.youtubevideo.flatMap(_.ids)

    val videoListedButNotDefined =
      listedYoutubeVideos.diff(definedYouTubeVideos).map(y => CheckError(YouTubeShortId(y), "listed but not defined"))
    val videoDefinedButNotListed =
      definedYouTubeVideos.diff(listedYoutubeVideos).map(y => CheckError(YouTubeShortId(y), "defined but not listed"))

    shortListedButNotDefined ++ shortDefinedButNotListed ++ videoListedButNotDefined ++ videoDefinedButNotListed
  }
}
