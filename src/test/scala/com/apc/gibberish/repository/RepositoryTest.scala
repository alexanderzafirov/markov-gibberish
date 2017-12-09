package com.apc.gibberish.repository

import com.apc.gibberish.GibberishSpec
import com.apc.gibberish.model.Gibberish

class RepositoryTest extends GibberishSpec with Connection with CreateTable with GibberishCalls {

  "The service" should {

    "retrieve no gibberish when there is none" in {
      retrieveAllGiberrish().items.isEmpty
    }

    "find all gibberish previous inserted" in {
      insertGibberish("apc", now)
      insertGibberish("ftw", now)
      retrieveAllGiberrish().items == List(Gibberish(1L, "apc", now), Gibberish(2L, "ftw", now))
    }

    "find gibberish by id" in {
      insertGibberish("apc", now)
      findGiberrishById(1L).get == Gibberish(1L, "apc", now)
    }

    "insert gibberish successfully" in {
      insertGibberish("apc", now) == 1L
    }
  }
}