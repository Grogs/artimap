package dao

import model.LatLong

import scala.concurrent.Future

trait GeocodingDaoInter {

  def retrieveLatLong(search: String): Future[LatLong]

}
