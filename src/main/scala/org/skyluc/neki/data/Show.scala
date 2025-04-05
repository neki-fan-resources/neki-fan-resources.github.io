package org.skyluc.neki.data

import java.nio.file.Path

case class ShowId(year: String, id: String) extends Id[Show] {
  import Show._
  override val uid = ID_BASE + year + Id.ID_SEPARATOR + id
  override val upath = ID_BASE_UPATH + year + Id.PATH_SEPARATOR + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(year).resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.shows.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced show '$year/$id' is not found"))
    }
  }
}

case class Show(
    id: ShowId,
    fullname: String,
    shortname: Option[String],
    sublabel: Option[String],
    date: Date,
    tour: Option[TourId],
    location: String,
    eventPage: Option[String], // TODO: use a special URL type ?
    setlistfm: Option[String],
    coverImage: CoverImage,
    multimedia: MultiMediaBlock,
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Item[Show]
    with WithCoverImage[Show] {
  override def errored(): Show = copy(error = true)
  override def withRelatedTo(id: Id[?]): Show = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }
}

object Show {
  val ID_BASE = "show_"
  val ID_BASE_UPATH = "show/"
  val ID_BASE_PATH = Path.of("show")

  val URL_BASE = "/show/"

  val FROM_KEY = "show"
}

case class Tour(
    id: TourId,
    fullname: String,
    shortname: Option[String],
    firstDate: Date,
    lastDate: Date,
    eventPage: Option[String],
    coverImage: CoverImage,
    shows: List[ShowId],
    error: Boolean = false,
    relatedTo: List[Id[?]] = Nil,
) extends Item[Tour]
    with WithCoverImage[Tour] {
  override def errored(): Tour = copy(error = true)
  override def withRelatedTo(id: Id[?]): Tour = {
    if (relatedTo.contains(id)) {
      this
    } else {
      copy(relatedTo = relatedTo :+ id)
    }
  }
}

case class TourId(id: String) extends Id[Tour] {
  import Tour._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)

  override def isKnown(sourceId: Id[?], data: Data): Option[DataError] = {
    if (data.tours.contains(this)) {
      None
    } else {
      Some(DataError(sourceId, s"Referenced tour '$id' is not found"))
    }
  }
}

object Tour {
  val ID_BASE = "tour_"
  val ID_BASE_UPATH = "tour/"
  val ID_BASE_PATH = Path.of("tour")

  val URL_BASE = "/tour/"

  val FROM_KEY = "tour"
}
