package com.apc.gibberish.repository

import com.apc.gibberish.model.{Gibberish, Gibberishes}
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}

object Repository extends GibberishCalls with Connection with CreateTable

trait GibberishCalls {

  def retrieveAllGiberrish(): Gibberishes = Gibberishes(Gibberish.findAll())

  def findGiberrishById(id: Long): Option[Gibberish] = Gibberish.findById(id)

  def insertGibberish(text: String, d: DateTime): Long =
    Gibberish.createWithAttributes('text -> text, 'createdAt -> d)
}

trait Connection {
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:skinny-mapper-test", "sa", "sa")
}

trait CreateTable {

  implicit val session: AutoSession.type = AutoSession

  sql"""
    create table gibberish (
      id bigint auto_increment primary key not null,
      text varchar(1000) not null,
      created_at timestamp not null
    )
    """.execute.apply()

  def dropTable(): Boolean =
    sql"""
      drop table gibberish
    """.execute.apply()
}