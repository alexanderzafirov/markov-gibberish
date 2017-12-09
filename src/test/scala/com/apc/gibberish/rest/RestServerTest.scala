package com.apc.gibberish.rest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.apc.gibberish.GibberishSpec
import com.apc.gibberish.model.{Gibberish, Gibberishes}
import com.apc.gibberish.repository.{Connection, GibberishCalls}

class RestServerTest extends GibberishSpec with ScalatestRouteTest with Router with Connection with GibberishCalls {

  "The service" should {

    "return a Gibberishes object containing an empty list response for initial GET request to /gibberishes" in {
      Get("/gibberishes") ~> route ~> check {
        responseAs[Gibberishes] shouldEqual Gibberishes(Nil)
      }
    }

    "return 404 for GET request to /gibberish with id that doesn't exist" in {
      Get("/gibberish/0") ~> route ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "return a Gibberishes object containing all previously inserted gibberish entries for GET request to /gibberishes" in {
      insertGibberish("apc", now)
      insertGibberish("ftw", now)
      Get("/gibberishes") ~> route ~> check {
        responseAs[Gibberishes] shouldEqual Gibberishes(List(Gibberish(1L, "apc", now), Gibberish(2, "ftw", now)))
      }
    }

    "return a Gibberish object with the given id containing the corresponding entry for GET request to /gibberish" in {
      insertGibberish("apc", now)
      Get("/gibberish/1") ~> route ~> check {
        responseAs[Gibberish] shouldEqual Gibberish(1L, "apc", now)
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> route ~> check {
        handled shouldBe false
      }
    }

    "return a 201 when posting gibberish successfully" in {
      Post("/gibberish?length=3", "apc apc com ftw ftw, lol lol yes no yes") ~> route ~> check {
        status shouldEqual StatusCodes.Created
      }
    }
  }
}
