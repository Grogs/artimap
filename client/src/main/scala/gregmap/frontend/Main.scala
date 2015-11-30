package gregmap.frontend

import org.scalajs.dom.raw.{Event, HTMLSelectElement}
import org.scalajs.dom.{console, document}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.{Dynamic, JSApp}

@JSExport
object Main extends JSApp {


  @JSExport
  def main(): Unit = {
    val selectedArticle = Option(document.getElementById("selected-article")).getOrElse(throw new IllegalStateException("Cannot find article selector")).asInstanceOf[HTMLSelectElement]
    val mapContainer = Option(document.getElementById("map-container")).getOrElse(throw new IllegalStateException("Map container div not found"))
    var map: Map = null

    def loadMap(articleId: String) = {
      console.log(s"make map - start - $articleId")
      map = new Map(articleId, mapContainer, Client)
      Dynamic.global.window.map = map.asInstanceOf[js.Any]
      console.log(s"make map - finish - $articleId")
    }

    def loadFrom(e: Event): Unit = loadMap(e.target.asInstanceOf[HTMLSelectElement].value)

    selectedArticle.onchange = loadFrom _
  }
}
