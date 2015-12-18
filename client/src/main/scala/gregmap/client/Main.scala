package gregmap.client

import gregmap.client.pages.Homepage
import org.scalajs.dom.raw.{Event, HTMLSelectElement, HashChangeEvent}
import org.scalajs.dom.{console, document, window}

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends App {

  @JSExport
  def main(): Unit = init(" ")

  @JSExport
  def init(containerId: String): Unit = {
    var map: Map = null
    val mapRoute = "/map(/.*)".r

    val container = document.getElementById(containerId)

    container.innerHTML = new Homepage().render.render

    val selectedArticle = Option(document.getElementById("selected-article")).getOrElse(throw new IllegalStateException("Cannot find article selector")).asInstanceOf[HTMLSelectElement]
    val mapContainer = Option(document.getElementById("map-container")).getOrElse(throw new IllegalStateException("Map container div not found"))

    def relPath(url: String) = if (url contains '#') url.dropWhile(_ != '#').drop(1) else ""
    def route(path: String) = path match {
      case mapRoute(article) =>
        console.log(s"mapRoute($article)")
        loadMap(article)
      case unmatched =>
        console.log(s"Failed to route: $path")
    }

    def loadMap(articleId: String) = {
      console.log(s"make map - start - $articleId")
      map = new Map(articleId, mapContainer, Client)
      Dynamic.global.window.map = map.asInstanceOf[js.Any]
      console.log(s"make map - finish - $articleId")
    }

    def loadFrom(e: Event): Unit = loadMap(e.target.asInstanceOf[HTMLSelectElement].value)

    selectedArticle.onchange = loadFrom _

    window.onhashchange = (e: HashChangeEvent) => route(relPath(e.newURL))

    console.log("add load listener")
    route(relPath(document.URL))
    document.body.addEventListener("load", (_:Event) => route(relPath(document.URL)))

  }

}
