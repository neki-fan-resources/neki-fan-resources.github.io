package org.skyluc.fan_resources.data

case class ShowId(year: String, id: String) extends ElementId[Show] {
  import Show._

  override val path = Path(ID_BASE_PATH, year, id)
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
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Element[Show] {

  override def process[T](processor: Processor[T]): T =
    processor.processShow(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processShow(this)
  }

  override def errored(): Show = copy(hasError = true)
  override def withLinkedTo(id: Id[?]*): Show = copy(linkedTo = mergeLinkedTo(id))
}

object Show {
  val ID_BASE_PATH = "show"

  val FROM_KEY = "show"
}

case class TourId(id: String) extends ElementId[Tour] {
  import Tour._
  override val path = Path(ID_BASE_PATH, id)
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
    hasError: Boolean = false,
    linkedTo: Seq[Id[?]] = Nil,
) extends Element[Tour] {

  override def process[T](processor: Processor[T]): T =
    processor.processTour(this)

  override def process[E, A](processor: ProcessorWithError[E, A]): Either[E, A] = {
    processor.processTour(this)
  }

  override def errored(): Tour = copy(hasError = true)
  override def withLinkedTo(id: Id[?]*): Tour = copy(linkedTo = mergeLinkedTo(id))
}

object Tour {
  val ID_BASE_PATH = "tour"

  val FROM_KEY = "tour"
}
