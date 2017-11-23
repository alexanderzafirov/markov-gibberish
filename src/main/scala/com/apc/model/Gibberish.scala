package com.apc.model

import org.joda.time.DateTime
import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

final case class Gibberish(id: Long, text: String, createdAt: DateTime)

final case class Gibberishes(items: List[Gibberish])

object Gibberish extends SkinnyCRUDMapper[Gibberish] {
  override lazy val defaultAlias = createAlias("g")

  override def extract(rs: WrappedResultSet, g: ResultName[Gibberish]): Gibberish = new Gibberish(
    id = rs.get(g.id),
    text = rs.get(g.text),
    createdAt = rs.get(g.createdAt)
  )
}

