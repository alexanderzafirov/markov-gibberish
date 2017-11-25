package com.apc.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapper}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

final case class Gibberish(id: Long, text: String, createdAt: DateTime)

final case class Gibberishes(items: List[Gibberish])

object Gibberish extends SkinnyCRUDMapper[Gibberish] {

  override lazy val defaultAlias: Alias[Gibberish] = createAlias("g")

  override def extract(rs: WrappedResultSet, g: ResultName[Gibberish]): Gibberish = new Gibberish(
    id = rs.get(g.id),
    text = rs.get(g.text),
    createdAt = rs.get(g.createdAt)
  )
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

    private val parser : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override def write(obj: DateTime) = JsString(parser.print(obj))

    override def read(json: JsValue) : DateTime = json match {
      case JsString(s) => parser.parseDateTime(s)
      case _ => throw new Exception("Malformed datetime")
    }
  }
  implicit val gibberishormat: RootJsonFormat[Gibberish] = jsonFormat3(Gibberish.apply)
  implicit val gibberishesFormat: RootJsonFormat[Gibberishes] = jsonFormat1(Gibberishes)
}