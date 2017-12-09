package com.apc.gibberish.markov

import scala.util.{Random, Try}

object MarkovGibberishGenerator {

  private[markov] def cleanWords(input: String): Array[String] =
    input.trim
      .toLowerCase()
      .split(" ")

  private[markov] def aggregatedWords(cleanWords: Array[String]): Map[String, Vector[String]] =
    cleanWords.sliding(2)
      .map(a => a(0) -> a(1))
      .foldLeft(Map[String, Vector[String]]()) { case (m, (k, v)) =>
        val values = m.getOrElse(k, Vector.empty[String])
        m + (k -> (values :+ v))
      }

  private[markov] def doMarkovRun(aggregatedWords: Map[String, Vector[String]], size: Int, r: Random = Random): Try[String] = Try {
    val n = r.nextInt(aggregatedWords.keys.size - 2)
    val keys = aggregatedWords.keySet.toVector
    var elements: Vector[String] = aggregatedWords(keys(n))
    var randomElement: String = elements(Random.nextInt(elements.size))
    var gibberishWords = Vector(randomElement)

    for (_ <- 0 to size) {
      elements = aggregatedWords(randomElement)
      randomElement = elements(Random.nextInt(elements.size))
      gibberishWords = gibberishWords :+ randomElement
    }

    gibberishWords.mkString(" ")
  }

  def generate(input: String, size: Int): Try[String] = {
    doMarkovRun(aggregatedWords(cleanWords(input)), size)
  }
}
