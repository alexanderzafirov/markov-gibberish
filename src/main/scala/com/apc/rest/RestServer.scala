package com.apc.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.apc.markov.MarkovGibberishGenerator
import com.apc.repository.DB
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

//TODO: Add tests
//TODO: Add Dockerfile
object RestServer extends App with Directives {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val typeSafeConfig = system.settings.config

  val route =
    path("gibberishes") {
      get {
        complete(
          DB.retrieveAllGiberrish().mapTo[ToResponseMarshallable]
        )
      }
    } ~
      path("gibberish") {
        get {
          parameter('id.as[Long]) { id =>
            complete(
              DB.findGiberrishById(id).mapTo[ToResponseMarshallable]
            )
          }
        } ~
          post {
            parameter('length.as[Int]) { length =>
              entity(as[String]) { input =>
                val eventualGibberish = Future(MarkovGibberishGenerator.generate(input, length))
                onComplete(eventualGibberish) {
                  case Success(Failure(_)) | Failure(_) => complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = "Couldn't generate gebberish for the given input/size. Try different input/size."
                    )
                  )
                  case Success(Success(text)) =>
                    onComplete(DB.createGibberish(text, DateTime.now))(_ => complete(HttpResponse(StatusCodes.Created, entity = text)))
                }
              }
            }
          }
      }

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
