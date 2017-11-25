package repository

import com.apc.model.Gibberish
import com.apc.repository.{Connection, CreateTables, GibberishCalls}
import org.scalatest._
import skinny._

class RepositoryTest extends FunSpec with Matchers with DBSettings with GibberishCalls with Connection with CreateTables {

  describe("Member") {
    it("should find all entities") {
      Gibberish.findAll().isEmpty
    }
  }
}

//import com.apc.model.Gibberish
//import com.apc.repository.{Connection, CreateTables, GibberishCalls, Repository}
//import org.scalatest._
//import org.scalatest.tools.Durations.Duration
//import skinny._
//import scalikejdbc._
//
//import scala.concurrent.Await
//import scala.concurrent.duration.Duration
//
//class RepositoryTest extends fixture.FunSpec with Matchers with DBSettings with GibberishCalls with Connection with CreateTables {
//
//  override def fixture(implicit session: DBSession) {
//    ""
//  }
//
//  describe("Member") {
//    it("should find all entities") { implicit session =>
//      Await.result(Repository.retrieveAllGiberrish(), Duration.Inf).items.size should be = 0
//    }
//  }
//}