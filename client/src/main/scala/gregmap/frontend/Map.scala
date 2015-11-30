package gregmap.frontend

import autowire._
import dao.{GeocodingDaoInter, TimeoutDaoInter}
import google.maps.InfoWindowOptions

import org.scalajs.dom.raw.Element

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js
import org.scalajs.dom.console

import scala.util.{Failure, Success}

@JSExport
class Map(val url: String, val target: Element, val getClient: Client) {
  val entries = getClient[TimeoutDaoInter].getEntries(url).call()

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
      points <- entries
    } yield for {
      entry <- points
    } yield for { //Using flatmap, will start one Future and wait for it to finish before starting the next
      pos <- getClient[GeocodingDaoInter].retrieveLatLong(entry.address).call()
      latLng = new google.maps.LatLng(pos.latitude, pos.longitude)
    } yield entry -> new google.maps.Marker(google.maps.MarkerOptions(
      position = latLng,
      map = gmap,
      title = entry.name
    ))
    res.flatMap( l => Future.sequence(l)).map(_.toArray)
  }

  markersFuture.map(_.foreach{ case (entry, marker) =>
    val infoWindow = new google.maps.InfoWindow(InfoWindowOptions( content = entry.markerHtml.render))
    marker.addListener("click", (_:js.Any) => infoWindow.open(gmap, marker))
  })

  markersFuture.onComplete{
    case Success(res) => console.log(s"Markers all successfully created. Count: ${res.length}")
    case Failure(ex) => console.log(s"Failed to retrieve markers:", ex.asInstanceOf[js.Any])
  }


}
