package org.skyluc.neki.data

import java.nio.file.Path

case class ShowId(year: String, id: String) extends Id {
  import Show._
  override val uid = ID_BASE + year + Id.ID_SEPARATOR + id
  override val upath = ID_BASE_UPATH + year + Id.PATH_SEPARATOR + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(year).resolve(id)
}

case class Show(
    id: ShowId,
    fullname: String,
    shortname: Option[String],
    date: Date,
    location: String,
    eventPage: Option[String], // TODO: use a special URL type ?
    setlistfm: Option[String],
    coverImage: CoverImage,
    error: Boolean = false,
) extends Item[Show]
    with WithCoverImage[Show] {
  override def errored(): Show = copy(error = true)
}

object Show {
  val ID_BASE = "show_"
  val ID_BASE_UPATH = "show/"
  val ID_BASE_PATH = Path.of("show")

  val URL_BASE = "/song/"
}

case class Tour(
    id: TourId,
    // fullname: String,
    // fullnameEn: Option[String],
    // album: Option[AlbumId],
    // releaseDate: Date,
    // credits: Option[Credits],
    coverImage: CoverImage,
    error: Boolean = false,
) extends Item[Tour]
    with WithCoverImage[Tour] {
  override def errored(): Tour = copy(error = true)
}

case class TourId(id: String) extends Id {
  import Tour._
  override val uid = ID_BASE + id
  override val upath = ID_BASE_UPATH + id + Id.PATH_SEPARATOR
  override def path = ID_BASE_PATH.resolve(id)
}

object Tour {
  val ID_BASE = "tour_"
  val ID_BASE_UPATH = "tour/"
  val ID_BASE_PATH = Path.of("tour")

  val URL_BASE = "/tour/"
}
