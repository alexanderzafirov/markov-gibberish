package com.apc.gibberish.markov

import org.scalatest.FlatSpec

import scala.util.Random

class MarkovGibberishGeneratorTest extends FlatSpec {

  "cleanWords" should "separate words by spaces, lowercase them and remove leading and trailing whitespaces" in {
    assert(MarkovGibberishGenerator.cleanWords(" This BanAna is crazy ") sameElements Array("this", "banana", "is", "crazy"))
  }

  "aggregatedWords" should "aggregate all the words in the given string according to the word they follow" in {
    assert(MarkovGibberishGenerator.aggregatedWords(Array("this", "banana", "this", "crazy")) == Map("this" -> Vector("banana", "crazy"), "banana" -> Vector("this")))
  }

  "doMarkovRun" should "do a markov run on a given map with a specified seed" in {
    assert(MarkovGibberishGenerator.doMarkovRun(Map("this" -> Vector("banana"), "banana" -> Vector("this"), "that" -> Vector("banana", "this")), 2, new Random(1123)).get == "banana this banana this")
  }
}
