package gregmap.client

import gregmap.client.pages.{Page, Homepage}
import org.scalajs.dom.raw.{Element, Event, HTMLSelectElement, HashChangeEvent}
import org.scalajs.dom.{console, document, window}

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.{Dynamic,debugger}
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {

  @JSExport
  def main(): Unit = ???

  @JSExport
  def init(containerId: String): Unit = {
    val mapRoute = "/map(/.*)".r

    val List(backStack, forwardStack) = List.fill(2)(new mutable.Stack[Page])
    var current: Page = null
    def navigate(page: Page): Unit = {
      backStack.push(current)
      current = page
      render(current, containerId)
    }

    val homepage = new Homepage()
    navigate(homepage)

    val mapContainer = Option(document.getElementById("map-container")).getOrElse(throw new IllegalStateException("Map container div not found"))
    def relPath(url: String) = if (url contains '#') url.dropWhile(_ != '#').drop(1) else ""

    def route(path: String) = path match {
      case mapRoute(article) =>
        console.log(s"mapRoute($article)")
        homepage.loadMap(article)
      case "" =>
        homepage.clearMap
      case unmatched =>
        console.log(s"Failed to route: $path")
        homepage.clearMap
    }

    window.onhashchange = (e: HashChangeEvent) => route(relPath(e.newURL))

    console.log("add load listener")
    route(relPath(document.URL))
    document.body.addEventListener("load", (_:Event) => route(relPath(document.URL)))

  }

  def render(page: Page, containerId: String): Unit = {
    val container = document.getElementById(containerId)
    container.innerHTML = ""
    container.appendChild(page.render.render)
  }
}
