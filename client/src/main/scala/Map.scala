import dao.{TimeoutDaoInter, GeocodingDaoInter}
import google.maps.LatLng
import org.scalajs.dom.raw.{Element, HTMLElement}

import scala.scalajs.js.annotation.JSExport

import autowire._

import org.scalajs.dom.document

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExport
class Map(val url: String, val target: Element)(val geocodingDao: Client[GeocodingDaoInter], val timeoutDao: Client[TimeoutDaoInter]) {
  val page = timeoutDao().getPage(url).call()
  val entries = page flatMap (timeoutDao().getEntries(url, _).call())

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
    pos <- entry.getLatLong(geocodingDao())
    latLng = new LatLng(pos.latitude, pos.longitude)
  } yield new google.maps.Marker(google.maps.MarkerOptions(
    position = latLng,
    map = gmap,
    title = entry.name
  ))

}
