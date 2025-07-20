package org.skyluc.neki_site.yaml

import org.skyluc.fan_resources.data as dfr
import org.skyluc.fan_resources.yaml as fr
import org.skyluc.fan_resources.yaml.ParserError
import org.skyluc.fan_resources.yaml.YamlKeys.*
import org.skyluc.neki_site.data as d
import org.skyluc.neki_site.yaml.YamlKeys.*
import org.skyluc.scala.EitherOps
import org.skyluc.yaml.*

import fr.DatumObjectDecoder
import fr.FrDecoders
import fr.ShowIdDecoder
import fr.TourIdDecoder
import fr.ElementGenIdDecoder

object PageIdDecoder extends ElementGenIdDecoder[d.Page](PAGE)

// TODO: duplicated in other sites
case class ChronologyPageBuilder(
    id: Option[String] = None,
    markers: Option[Seq[dfr.Id[?]]] = None,
    startDate: Option[dfr.Date] = None,
    endDate: Option[dfr.Date] = None,
    linkedTo: Option[Seq[dfr.Id[?]]] = None,
    errors: Seq[ParserError] = Seq(),
) extends fr.DatumBuilder[d.ChronologyPage, ChronologyPageBuilder] {

  import ObjectBuilder.*

  override def setLinkedTo(linkedTo: Option[Seq[dfr.Id[?]]]): ChronologyPageBuilder = copy(linkedTo = linkedTo)

  override def setErrors(errs: Seq[ParserError]): ChronologyPageBuilder = copy(errors = errors :++ errs)

  override protected def build(using
      context: DecoderContext[FrDecoders]
  ): Either[Seq[ParserError], d.ChronologyPage] = {
    for {
      id <- checkDefined(ID, id)
      itemIds <- checkDefined(MARKERS, markers)
      startDate <- checkDefined(START_DATE, startDate)
      endDate <- checkDefined(END_DATE, endDate)
      markerIds <- itemIdsToMarkerIds(itemIds)
    } yield {
      d.ChronologyPage(
        d.PageId(id),
        dfr.Chronology(
          markerIds,
          startDate,
          endDate,
        ),
        linkedTo.getOrElse(Nil),
      )
    }
  }

  private def itemIdsToMarkerIds(itemIds: Seq[dfr.Id[?]])(using
      context: DecoderContext[FrDecoders]
  ): Either[Seq[ParserError], Seq[dfr.ChronologyMarkerId]] = {

    val res: Seq[Either[ParserError, dfr.ChronologyMarkerId]] = itemIds.map {
      case a: dfr.AlbumId =>
        Right(dfr.AlbumMarkerId(a))
      case e: dfr.EventId =>
        Right(dfr.EventMarkerId(e))
      case m: dfr.MediaId =>
        Right(dfr.MediaMarkerId(m))
      case m: dfr.MultiMediaId[?] =>
        Right(dfr.MultiMediaMarkerId(m))
      case s: dfr.ShowId =>
        Right(dfr.ShowMarkerId(s))
      case s: dfr.SongId =>
        Right(dfr.SongMarkerId(s))
      case t: dfr.TourId =>
        Right(dfr.TourMarkerId(t))
      case id =>
        Left(ParserError(context.filename, Some(s"Unsupported item id for marker: $id")))
    }

    val (errors, markerIds) = EitherOps.errorsAndValues(res)

    if (errors.isEmpty) {
      Right(markerIds)
    } else {
      Left(errors)
    }
  }
}

object ChronologyPageDecoder extends DatumObjectDecoder[d.ChronologyPage, ChronologyPageBuilder] {

  override def datumAttributes(using
      context: DecoderContext[FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.ChronologyPage, ChronologyPageBuilder, FrDecoders]] = Seq(
    YamlObjectAttribute(
      ID,
      StringDecoder(),
      (b, v) => b.copy(id = v),
    ),
    YamlObjectAttribute(
      MARKERS,
      ListDecoder(new DispatchDecoder[dfr.Id[?], FrDecoders] {
        override def dispatch(using
            context: DecoderContext[FrDecoders]
        ): Map[String, YamlDecoder[? <: dfr.Id[?], FrDecoders]] =
          context.decoders.markerItem
      }),
      (b, v) => b.copy(markers = v),
    ),
    YamlObjectAttribute(
      START_DATE,
      fr.DateDecoder,
      (b, v) => b.copy(startDate = v),
    ),
    YamlObjectAttribute(
      END_DATE,
      fr.DateDecoder,
      (b, v) => b.copy(endDate = v),
    ),
  )

  override def zero(): ChronologyPageBuilder = ChronologyPageBuilder()

}

case class MusicPageBuilder(
    id: Option[String] = None,
    dark: Option[Boolean] = None,
    music: Option[Seq[dfr.SongId | dfr.AlbumId]] = None,
    linkedTo: Option[Seq[dfr.Id[?]]] = None,
    errors: Seq[ParserError] = Nil,
) extends fr.DatumBuilder[d.MusicPage, MusicPageBuilder] {

  import ObjectBuilder.*

  override def setLinkedTo(linkedTo: Option[Seq[dfr.Id[?]]]): MusicPageBuilder = copy(linkedTo = linkedTo)

  override def setErrors(errs: Seq[ParserError]): MusicPageBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[fr.FrDecoders]): Either[Seq[ParserError], d.MusicPage] = {
    for {
      id <- checkDefined(ID, id)
      music <- checkDefined(MUSIC, music)
    } yield {
      d.MusicPage(
        d.PageId(id, dark.getOrElse(false)),
        music,
        linkedTo.getOrElse(Nil),
      )
    }
  }
}

object SongAlbumIdDecoder extends DispatchDecoder[dfr.SongId | dfr.AlbumId, FrDecoders] {

  override def dispatch(using
      context: DecoderContext[FrDecoders]
  ): Map[String, YamlDecoder[? <: dfr.SongId | dfr.AlbumId, FrDecoders]] =
    Map(
      (SONG, fr.SongIdDecoder),
      (ALBUM, fr.AlbumIdDecoder),
    )

}

object MusicPageDecoder extends DatumObjectDecoder[d.MusicPage, MusicPageBuilder] {

  override def datumAttributes(using
      context: DecoderContext[fr.FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.MusicPage, MusicPageBuilder, fr.FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        ID,
        StringDecoder(),
        (b, v) => b.copy(id = v),
      ),
      YamlObjectAttribute(
        DARK,
        BooleanDecoder(),
        (b, v) => b.copy(dark = v),
      ),
      YamlObjectAttribute(
        MUSIC,
        ListDecoder(SongAlbumIdDecoder),
        (b, v) => b.copy(music = v),
      ),
    )

  override def zero(): MusicPageBuilder = MusicPageBuilder()

}

case class ShowsPageBuilder(
    id: Option[String] = None,
    shows: Option[Seq[dfr.ShowId | dfr.TourId]] = None,
    linkedTo: Option[Seq[dfr.Id[?]]] = None,
    errors: Seq[ParserError] = Nil,
) extends fr.DatumBuilder[d.ShowsPage, ShowsPageBuilder] {

  import ObjectBuilder.*

  override def setLinkedTo(linkedTo: Option[Seq[dfr.Id[?]]]): ShowsPageBuilder = copy(linkedTo = linkedTo)

  override def setErrors(errs: Seq[ParserError]): ShowsPageBuilder = copy(errors = errors :++ errs)

  override protected def build(using context: DecoderContext[fr.FrDecoders]): Either[Seq[ParserError], d.ShowsPage] = {
    for {
      id <- checkDefined(ID, id)
      shows <- checkDefined(MUSIC, shows)
    } yield {
      d.ShowsPage(
        d.PageId(id),
        shows,
        linkedTo.getOrElse(Nil),
      )
    }
  }
}

object ShowTourIdDecoder extends DispatchDecoder[dfr.ShowId | dfr.TourId, FrDecoders] {

  override def dispatch(using
      context: DecoderContext[FrDecoders]
  ): Map[String, YamlDecoder[? <: dfr.ShowId | dfr.TourId, FrDecoders]] =
    Map(
      (SHOW, ShowIdDecoder),
      (TOUR, TourIdDecoder),
    )

}

object ShowsPageDecoder extends DatumObjectDecoder[d.ShowsPage, ShowsPageBuilder] {

  override def datumAttributes(using
      context: DecoderContext[fr.FrDecoders]
  ): Seq[YamlObjectAttributeProcessor[?, d.ShowsPage, ShowsPageBuilder, fr.FrDecoders]] =
    Seq(
      YamlObjectAttribute(
        ID,
        StringDecoder(),
        (b, v) => b.copy(id = v),
      ),
      YamlObjectAttribute(
        SHOWS,
        ListDecoder(ShowTourIdDecoder),
        (b, v) => b.copy(shows = v),
      ),
    )

  override def zero(): ShowsPageBuilder = ShowsPageBuilder()

}
