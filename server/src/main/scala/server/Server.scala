package server

import akka.actor.ActorSystem
import server.views.Index
import shared.dao.{GeocodingDaoInter, TimeoutDaoInter}
import shared.service.MapServiceInter
import upickle.{default => upickle}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer


import scala.util.Properties

class Server(config: Config) {
  object Router extends autowire.Server[String, upickle.Reader, upickle.Writer] {
    def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)

    def write[Result: upickle.Writer](r: Result) = upickle.write(r)
  }

  implicit val system = ActorSystem("artimap-server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val port = Properties.envOrElse("PORT", "8080").toInt

  def req(s: List[String], e: String) = autowire.Core.Request(
    s,
    upickle.read[Map[String, String]](e)
  )

  val timeoutRouter = Router.route[TimeoutDaoInter](config.timeoutDao)
  val geocodingRouter = Router.route[GeocodingDaoInter](config.geocodingDao)
  val mapServiceRouter = Router.route[MapServiceInter](config.mapService)

  val routes = {
    (get & path("flush")) {
      complete {
        config.flushCaches()
        "Done!"
      }
    } ~
    (get & path("nuke" / Segments)) { s =>
      complete {
        val article = "/" + s.mkString("/")
        config.mapService.nuke(article)
        config.timeoutDao.nuke(article)
        s"Removed $article"
      }
    } ~
    (get & (path("index") | path(""))) {
      complete (
        HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          Index(config.googleKey, config.environment).render
        )
      )
    } ~
    pathPrefix("assets") {
      getFromResourceDirectory("js")
    } ~
    (post & path("api" / Segments)) { s =>
        entity(as[String]) { e =>
          complete {
            (timeoutRouter orElse geocodingRouter orElse mapServiceRouter)(req(s,e))
          }
        }
      }

  }
}

object Server extends Server(config = Config) with App {
  Http().bindAndHandle(routes, "localhost", port)
}
