package com.apc.gibberish.repository

import com.apc.gibberish.GibberishSpec
import com.apc.gibberish.model.Gibberish

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class RepositoryTest extends GibberishSpec with Connection with CreateTables with GibberishCalls {

  "The service" should {

    "retrieve no gibberish when there is none" in {
      Await.result(retrieveAllGiberrish(), Duration.Inf).items.isEmpty
    }

    "find all gibberish previous inserted" in {
      Await.result(insertGibberish("apc", now), Duration.Inf)
      Await.result(insertGibberish("ftw", now), Duration.Inf)
      Await.result(retrieveAllGiberrish(), Duration.Inf).items == List(Gibberish(1L, "apc", now), Gibberish(2, "ftw", now))
    }

    "find gibberish by id" in {
      Await.result(insertGibberish("apc", now), Duration.Inf)
      Await.result(findGiberrishById(1L), Duration.Inf).get == Gibberish(1L, "apc", now)
    }

    "insert gibberish successfully" in {
      Await.result(insertGibberish("apc", now), Duration.Inf) == 1L
    }
  }
}