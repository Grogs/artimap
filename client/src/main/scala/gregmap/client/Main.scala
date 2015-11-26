package gregmap.client

import scala.scalajs.js
import org.scalajs.dom.document
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * Created by Greg Dorrell on 26/11/2015.
  */
@JSExport
object Main extends JSApp {
  @JSExport
  def main(): Unit = {
    new Map("dummy", document.getElementById("map-container"))(Client)
  }
}
