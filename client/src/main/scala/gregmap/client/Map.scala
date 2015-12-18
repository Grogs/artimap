package gregmap.client

import autowire._
import google.maps.InfoWindowOptions
import org.scalajs.dom.console
import org.scalajs.dom.raw.Element
import shared.service.MapServiceInter

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

@JSExport
class Map(val url: String, val target: Element, val getClient: Client) {

  val centerPoint = new google.maps.LatLng(51.51570645276635, -0.12139606201174047)
  val opts = google.maps.MapOptions(
    center = centerPoint,
    zoom = 12,
    panControl = false,
    streetViewControl = false,
    mapTypeControl = false)

  val gmap = new google.maps.Map(target, opts)

  val markersFuture = {
    val res = for {
      entries <- getClient[MapServiceInter].getPositionedEntries(url).call()
    } yield for { //Using flatmap, will start one Future and wait for it to finish before starting the next
      (entry, pos) <- entries
      latLng = new google.maps.LatLng(pos.latitude, pos.longitude)
    } yield (entry, pos, new google.maps.Marker(google.maps.MarkerOptions(
      position = latLng,
      map = gmap,
      title = entry.name
    )))
    res.map(_.toArray)
  }

  val markerBounds = new google.maps.LatLngBounds()
  var activeInfoWindow = new google.maps.InfoWindow

  markersFuture.map(_.foreach{ case (entry, pos, marker) =>
    val infoWindow = new google.maps.InfoWindow(InfoWindowOptions( content = entry.markerHtml(pos).render))
    marker.addListener("click", (_:js.Any) => {
      activeInfoWindow.close()
      activeInfoWindow = infoWindow
      infoWindow.open(gmap, marker)
    })
    markerBounds.extend(marker.getPosition())
  }).foreach(_ => gmap.fitBounds(markerBounds))

  markersFuture.onComplete{
    case Success(res) => console.log(s"Markers all successfully created. Count: ${res.length}")
    case Failure(ex) => console.log(s"Failed to retrieve markers:", ex.asInstanceOf[js.Any])
  }


}
