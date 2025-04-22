package org.skyluc.neki_site.html

import org.skyluc.fan_resources.Common

object TitleAndDescription {

  val TITLE_LENGTH_MAX = 70
  val TITLE_SUFFIX_1 = " - NEK!"
  val TITLE_SUFFIX_12 = TITLE_SUFFIX_1 + " - NEK! Fan Resources."
  val TITLE_LENGTH_MAX_1 = TITLE_LENGTH_MAX - TITLE_SUFFIX_1.length()
  val TITLE_LENGHT_MAX_12 = TITLE_LENGTH_MAX - TITLE_SUFFIX_12.length()
  val DESCRIPTION_LENGTH_MAX = 150
  val DESCRIPTION_SUFFIX_13 = TITLE_SUFFIX_1 + " Resources around the band NEK!."
  val DESCRIPTION_SUFFIX_123 = TITLE_SUFFIX_12 + " Resources around the band NEK!."
  val DESCRIPTION_SUFFIX_1234 = DESCRIPTION_SUFFIX_123 + " Lyrics, videos, live, concerts, history."
  val DESCRIPTION_LENGTH_MAX_1 = DESCRIPTION_LENGTH_MAX - TITLE_SUFFIX_1.length()
  val DESCRIPTION_LENGTH_MAX_13 = DESCRIPTION_LENGTH_MAX - DESCRIPTION_SUFFIX_13.length()
  val DESCRIPTION_LENGTH_MAX_123 = DESCRIPTION_LENGTH_MAX - DESCRIPTION_SUFFIX_123.length()
  val DESCRIPTION_LENGTH_MAX_1234 = DESCRIPTION_LENGTH_MAX - DESCRIPTION_SUFFIX_1234.length()

  val SEPARATOR = " - "
  val SPACE = " "
  val EXTRA = Some("extra")

  def formattedTitle(
      designation: Option[String],
      designationSuffix: Option[String],
      fullname: String,
      fullnameEn: Option[String],
      shortName: Option[String],
      altName: Option[String],
  ): String = {
    val chosen = baseOptions(designation, designationSuffix, fullname, fullnameEn, shortName)
      .filter(_.length() < TITLE_LENGTH_MAX)
      .sortBy(_.length())
      .last

    val chosenLength = chosen.length()
    if (chosenLength > TITLE_LENGTH_MAX) {
      println(s"'$chosen' is too long for a title")
      chosen.takeRight(TITLE_LENGTH_MAX)
    } else if (chosenLength < TITLE_LENGHT_MAX_12) {
      chosen + TITLE_SUFFIX_12
    } else if (chosenLength < TITLE_LENGTH_MAX_1) {
      chosen + TITLE_SUFFIX_1
    } else {
      chosen
    }
  }

  def formattedDescription(
      designation: Option[String],
      designationSuffix: Option[String],
      fullname: String,
      fullnameEn: Option[String],
      shortName: Option[String],
      altName: Option[String],
  ): String = {
    val chosen = baseOptions(designation, designationSuffix, fullname, fullnameEn, shortName)
      .filter(_.length() < DESCRIPTION_LENGTH_MAX)
      .sortBy(_.length())
      .last

    val chosenLength = chosen.length()
    val candidate = if (chosenLength > DESCRIPTION_LENGTH_MAX) {
      println(s"'$chosen' is too long for a description")
      chosen.takeRight(DESCRIPTION_LENGTH_MAX)
    } else if (chosenLength < DESCRIPTION_LENGTH_MAX_1234) {
      chosen + DESCRIPTION_SUFFIX_1234
    } else if (chosenLength < DESCRIPTION_LENGTH_MAX_123) {
      chosen + DESCRIPTION_SUFFIX_123
    } else if (chosenLength < DESCRIPTION_LENGTH_MAX_13) {
      chosen + DESCRIPTION_SUFFIX_13
    } else if (chosenLength < TITLE_LENGTH_MAX_1) {
      chosen + TITLE_SUFFIX_1
    } else {
      chosen
    }

    altName
      .map(an =>
        val withAlt = candidate + " " + an + "."
        if (withAlt.length() < DESCRIPTION_LENGTH_MAX) {
          withAlt
        } else {
          candidate
        }
      )
      .getOrElse(candidate)
  }

  private def baseOptions(
      designation: Option[String],
      designationSuffix: Option[String],
      fullname: String,
      fullnameEn: Option[String],
      shortName: Option[String],
  ): Seq[String] = {
    val designationStr = designation
      .map { d =>
        SEPARATOR + d.capitalize + designationSuffix
          .map { ds =>
            SPACE + ds
          }
          .getOrElse(Common.EMPTY)
      }
      .getOrElse(Common.EMPTY)
    val fullnameEnStr = fullnameEn.map(n => s" ($n)").getOrElse(Common.EMPTY)
    Seq(fullname + fullnameEnStr + designationStr, fullname + designationStr, fullname)
      ++ shortName
        .map(s =>
          Seq(
            s + fullnameEnStr + designationStr,
            s + designationStr,
            s,
          )
        )
        .getOrElse(Nil)
  }

}
