package com.apc.gibberish.repository

import com.apc.gibberish.model.{Gibberish, Gibberishes}
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Repository extends GibberishCalls with Connection with CreateTables

trait GibberishCalls {

  def retrieveAllGiberrish() = Future(Gibberishes(Gibberish.findAll()))

  def findGiberrishById(id: Long) = Future(Gibberish.findById(id))

  def insertGibberish(text: String, d: DateTime): Future[Long] =
    Future(Gibberish.createWithAttributes('text -> text, 'createdAt -> d))
}

trait Connection {
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:skinny-mapper-test", "sa", "sa")
}

trait CreateTables {

  implicit val session: AutoSession.type = AutoSession

  sql"""
create table gibberish (
  id bigint auto_increment primary key not null,
  text varchar(1000) not null,
  created_at timestamp not null
)
    """.execute.apply()
}