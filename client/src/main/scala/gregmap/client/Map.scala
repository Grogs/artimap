package gregmap.client

import autowire._
import dao.{GeocodingDaoInter, TimeoutDaoInter}
import google.maps.LatLng
import org.scalajs.dom.raw.Element

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
class Map(val url: String, val target: Element)(val client: Client) {
  val page = client[TimeoutDaoInter].getPage(url).call()
  val entries = page flatMap ( p => client[TimeoutDaoInter].getEntries(url, p).call())

  js.debugger()

  val opts = google.maps.MapOptions(
    center = new LatLng(51.201203, -1.724370),
    zoom = 8,
    panControl = false,
    streetViewControl = false,
    mapTypeControl = false)

  val gmap = new google.maps.Map(target, opts)

  val markers = for {
    points <- entries
  } yield for {
    entry <- points
  } yield for { //Using flatmap, will start one Future and wait for it to finish before starting the next
    pos <- client[GeocodingDaoInter].retrieveLatLong(entry.address).call()
    latLng = new LatLng(pos.latitude, pos.longitude)
  } yield new google.maps.Marker(google.maps.MarkerOptions(
    position = latLng,
    map = gmap,
    title = entry.name
  ))

}
