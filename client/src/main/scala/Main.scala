import dao.{TimeoutDaoInter, GeocodingDaoInter}
import org.scalajs.dom.document
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main {
  @JSExport
  def main(): Unit = {
    val geoDao: Client[GeocodingDaoInter] = Client.geocodingDaoClient
    val timeoutDao: Client[TimeoutDaoInter] = Client.timeoutDaoClient
    new Map("dummy", document.getElementById("map-container"))(geoDao, timeoutDao)
  }
}
