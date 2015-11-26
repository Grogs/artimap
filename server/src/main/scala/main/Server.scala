package main

import akka.actor.ActorSystem
import dao.{GeocodingDaoInter, TimeoutDaoInter}
import spray.http.{MediaTypes, HttpEntity}
import spray.routing.SimpleRoutingApp
import upickle.{default => upickle}

import scala.util.Properties

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("on-spray-can")

  val port = Properties.envOrElse("PORT", "8080").toInt

  def req(s: List[String], e: String) = autowire.Core.Request(
    s,
    upickle.read[Map[String, String]](e)
  )

  val timeoutRouter = Router.route[TimeoutDaoInter](Config.timeoutDao)
  val geocodingRouter = Router.route[GeocodingDaoInter](Config.geocodingDao)

  startServer(interface = "localhost", port = port) {
    path("index") {
      get {
        complete {
          val doc = Index().render
          HttpEntity(
            MediaTypes.`text/html`,
            doc
          )
        }
      }
    } ~
    pathPrefix("assets") { getFromResourceDirectory("") } ~
    (post & path("api" / Segments)) {s =>
      extract(_.request.entity.asString) { e =>
        complete {
          (timeoutRouter orElse geocodingRouter)(req(s,e))
        }
      }
    }
  }
}

object Router extends autowire.Server[String, upickle.Reader, upickle.Writer] {
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)

  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}
