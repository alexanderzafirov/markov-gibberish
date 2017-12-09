package com.apc.gibberish

import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

trait GibberishSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  val now: DateTime = DateTime.parse("2017-11-25T14:17:36")
}
