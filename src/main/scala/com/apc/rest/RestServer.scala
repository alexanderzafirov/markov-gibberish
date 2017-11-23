package com.apc.rest

import akka.actor.ActorSystem
import akka.dispatch.sysmsg.Create
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.apc.markov.MarkovGibberish
import spray.json._

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

//TODO: Add db layer
//TODO: Add tests
//TODO: Add Dockerfile
//FIXME: Clean up useless code
// domain model
final case class Item(name: String, id: Long)

final case class Order(items: List[Item])

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order) // contains List[Item]
}

object RestServer extends App with Directives with JsonSupport {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val typeSafeConfig = system.settings.config

  val route =
    path("gibberish") {
      get {
        complete(Item("thing", 42))
      } ~
        post {
          parameter('length.as[Int]) { length =>
            entity(as[String]) { input =>
              val eventualGibberish = Future(MarkovGibberish.generate(input, length))
              onComplete(eventualGibberish) {
                case Success(Failure(_)) | Failure(_) => complete(
                  HttpResponse(
                    StatusCodes.InternalServerError,
                    entity = "Couldn't generate gebberish for the given input/size. Try different input/size."
                  )
                )
                case Success(Success(v)) => complete(HttpResponse(StatusCodes.Created, entity = v))
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
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
