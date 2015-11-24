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


  startServer("0.0.0.0", port = port) {
    (get & path("/")) {
      complete {
        import scalatags.Text.all._
        val doc = html(
//          head(),
          body(
            div(id:="map", style:="height: 100%;")
          )
        ).render
        HttpEntity(
          MediaTypes.`text/html`,
          doc
        )

      }
    } ~
    path("api") {
      (get & path("entries" / Segments)) { s =>
        extract(_.request.entity.asString) { e =>
          complete {
            Router.route[TimeoutDaoInter](Config.timeoutDao)(req(s,e))
          }
        }
      } ~
      (get & path("geocode" / Segments)) { s =>
        extract(_.request.entity.asString) { e =>
          complete {
            Router.route[GeocodingDaoInter](Config.geocodingDao)(req(s,e))
          }
        }
      }
    }
  }
}

object Router extends autowire.Server[String, upickle.Reader, upickle.Writer] {
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)

  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}
