lazy val root = (project in file("."))
  .settings(
    name         := "markov_gibberish",
    organization := "com.apc",
    scalaVersion := "2.12.4",
    version      := "0.1"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"
)
