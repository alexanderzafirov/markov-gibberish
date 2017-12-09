package com.apc.gibberish.rest

import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.apc.gibberish._
import com.apc.gibberish.markov.MarkovGibberishGenerator
import com.apc.gibberish.model.JsonSupport
import com.apc.gibberish.repository.Repository
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Router extends Directives with JsonSupport {

  implicit val blockingDispatcher: MessageDispatcher = system.dispatchers.lookup("my-blocking-dispatcher")

  val route: Route =
    path("gibberishes") {
      get {
        onComplete(Future(Repository.retrieveAllGiberrish())) {
          case Failure(_) => complete(StatusCodes.InternalServerError)
          case Success(g) => complete(g)
        }
      }
    } ~
      path("gibberish" / IntNumber) { id =>
        get {
          onComplete(Future(Repository.findGiberrishById(id))) {
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
                onComplete(Future(Repository.insertGibberish(text, DateTime.now))) {
                  case Failure(_) => complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = "Requested input is too large to store. Please select a smaller input length."
                    )
                  )
                  case Success(l) =>
                    extractRequestContext { requestContext =>
                      val request = requestContext.request
                      val location = request.uri.copy(path = request.uri.path / l.toString, rawQueryString = None)
                      respondWithHeader(Location(location)) {
                        complete(HttpResponse(StatusCodes.Created, entity = text))
                      }
                    }
                }
            }
          }
        }
      }

}
