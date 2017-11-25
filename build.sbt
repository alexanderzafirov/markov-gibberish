lazy val root = (project in file("."))
  .settings(
    name := "markov_gibberish",
    organization := "com.apc",
    scalaVersion := "2.12.4",
    version := "0.1"
  )

val akkaHttpDeps = Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"
)
libraryDependencies ++= akkaHttpDeps

val skinnyDeps = Seq(
  "org.skinny-framework" %% "skinny-orm" % "2.5.2",
  "com.h2database" % "h2" % "1.4.+",
  "ch.qos.logback" % "logback-classic" % "1.1.+"
)
libraryDependencies ++= skinnyDeps

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.4" % "test"
