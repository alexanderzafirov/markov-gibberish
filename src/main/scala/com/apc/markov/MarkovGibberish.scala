package com.apc.markov

import scala.util.Random

object MarkovGibberish {

  def generator(input: String, size: Int): String = {
    val cleanWords = input.trim
      .replaceAll("[!,.;\"]", "")
      .toLowerCase()
      .split(" ")

    val aggregatedWords = cleanWords
      .sliding(2)
      .map(a => a(0) -> a(1))
      .foldLeft(Map[String, Vector[String]]()) { case (m, (k, v)) =>
        val values = m.getOrElse(k, Vector.empty[String])
        m + (k -> (values :+ v))
      }

    //TODO: Think of multithreaded environment
    val n = Random.nextInt(aggregatedWords.keys.size - 2)
    var elements: Vector[String] = aggregatedWords(cleanWords(n))
    var randomElement: String = elements(Random.nextInt(elements.size))
    var gibberishWords = Vector(randomElement)

    for (_ <- 0 to size) {
      elements = aggregatedWords(randomElement)
      randomElement = elements(Random.nextInt(elements.size))
      gibberishWords = gibberishWords :+ randomElement
    }

    //TODO: add random capitalization and punctuation
    gibberishWords.mkString(", ")
  }
}
