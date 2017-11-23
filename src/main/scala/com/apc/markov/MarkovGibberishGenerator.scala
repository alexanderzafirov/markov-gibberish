package com.apc.markov

import scala.util.{Random, Try}

object MarkovGibberishGenerator {

  def generate(input: String, size: Int): Try[String] = {

    val cleanWords = input.trim
      .toLowerCase()
      .split(" ")

    val aggregatedWords = cleanWords.sliding(2)
      .map(a => a(0) -> a(1))
      .foldLeft(Map[String, Vector[String]]()) { case (m, (k, v)) =>
        val values = m.getOrElse(k, Vector.empty[String])
        m + (k -> (values :+ v))
      }

    def doMarkovRun(aggregatedWords: Map[String, Vector[String]]): Try[String] = Try {
      val n = Random.nextInt(aggregatedWords.keys.size - 2)
      var elements: Vector[String] = aggregatedWords(cleanWords(n))
      var randomElement: String = elements(Random.nextInt(elements.size))
      var gibberishWords = Vector(randomElement)

      for (_ <- 0 to size) {
        elements = aggregatedWords(randomElement)
        randomElement = elements(Random.nextInt(elements.size))
        gibberishWords = gibberishWords :+ randomElement
      }

      gibberishWords.mkString(" ")
    }

    doMarkovRun(aggregatedWords)
  }
}
