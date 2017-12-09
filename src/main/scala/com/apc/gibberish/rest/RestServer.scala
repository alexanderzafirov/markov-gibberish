package com.apc.gibberish.rest

import akka.http.scaladsl.Http
import com.apc.gibberish._

import scala.io.StdIn

object RestServer extends App with Router {

  val typeSafeConfig = system.settings.config

  val bindingFuture = Http().bindAndHandle(
    route,
    typeSafeConfig.getString("akka.http.server.interface"),
    typeSafeConfig.getInt("akka.http.server.port")
  )

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate())
}
