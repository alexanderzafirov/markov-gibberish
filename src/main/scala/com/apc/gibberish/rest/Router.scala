package com.apc.gibberish.rest

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import com.apc.gibberish.markov.MarkovGibberishGenerator
import com.apc.gibberish.repository.Repository
import org.joda.time.DateTime
import akka.http.scaladsl.server.{Directives, Route}
import com.apc.gibberish.model.JsonSupport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Router extends Directives with JsonSupport {

  val route: Route =
    path("gibberishes") {
      get {
        onComplete(Repository.retrieveAllGiberrish()) {
          case Failure(_) => complete(StatusCodes.InternalServerError)
          case Success(g) => complete(g)
        }
      }
    } ~
      path("gibberish") {
        get {
          parameter('id.as[Long]) { id =>
            onComplete(Repository.findGiberrishById(id)) {
              case Failure(_) => complete(StatusCodes.InternalServerError)
              case Success(None) => complete(StatusCodes.NotFound)
              case Success(g) => complete(g)
            }
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
                      entity = "Couldn't generate gibberish for the given input/size. Try different input/length."
                    )
                  )
                  case Success(Success(text)) =>
                    onComplete(Repository.insertGibberish(text, DateTime.now)) {
                      case Failure(_) => complete(
                        HttpResponse(
                          StatusCodes.InternalServerError,
                          entity = "Requested input is too large to store. Please select a smaller input length."
                        )
                      )
                      case Success(_) => complete(HttpResponse(StatusCodes.Created, entity = text))
                    }
                }
              }
            }
          }
      }
}
