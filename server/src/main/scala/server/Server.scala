package server

import akka.actor.ActorSystem
import server.views.Index
import shared.dao.{GeocodingDaoInter, TimeoutDaoInter}
import shared.service.MapServiceInter
import spray.http.{HttpEntity, MediaTypes}
import spray.routing.SimpleRoutingApp
import upickle.{default => upickle}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

class Server(config: Config) extends SimpleRoutingApp {
  object Router extends autowire.Server[String, upickle.Reader, upickle.Writer] {
    def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)

    def write[Result: upickle.Writer](r: Result) = upickle.write(r)
  }

  implicit val system = ActorSystem("spray-server")

  val port = Properties.envOrElse("PORT", "8080").toInt

  def req(s: List[String], e: String) = autowire.Core.Request(
    s,
    upickle.read[Map[String, String]](e)
  )

  val timeoutRouter = Router.route[TimeoutDaoInter](config.timeoutDao)
  val geocodingRouter = Router.route[GeocodingDaoInter](config.geocodingDao)
  val mapServiceRouter = Router.route[MapServiceInter](config.mapService)

  startServer(interface = "0.0.0.0", port = port) {
    (get & path("flush")) {
      complete {
        config .flushCaches()
        "Done!"
      }
    } ~
    (get & (path("index") | path(""))) {
      complete {
        val doc = Index(config.googleKey, config.environment).render
        HttpEntity(
          MediaTypes.`text/html`,
          doc
        )
      }
    } ~
    pathPrefix("assets") {
      getFromResourceDirectory("js")
    } ~
    (post & path("api" / Segments)) { s =>
        extract(_.request.entity.asString) { e =>
          complete {
            (timeoutRouter orElse geocodingRouter orElse mapServiceRouter)(req(s,e))
          }
        }
      }

  }
}

object Server extends Server(config = Config) with App
