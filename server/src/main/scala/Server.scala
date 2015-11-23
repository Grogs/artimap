import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

import scala.util.Properties

import upickle.{default=>upickle}

object Server extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("on-spray-can")

  val port = Properties.envOrElse("PORT", "8080").toInt

  startServer("0.0.0.0", port = port) {
    (get & path("map" / Segments)) { s =>
      extract(_.request.entity.asString) { e =>
        complete {
          Router.route[TimeoutDaoInter](Config.timeoutDao)
        }
      }
    }
  }
}

object Router extends autowire.Server[String, upickle.Reader, upickle.Writer]{
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}
