package com.apc.gibberish

import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}

trait GibberishSpec extends WordSpec with Matchers {

  val now: DateTime = DateTime.parse("2017-11-25T14:17:36")
}
