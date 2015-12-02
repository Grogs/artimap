package shared.dao

import shared.model.LatLong

import scala.concurrent.Future

trait GeocodingDaoInter {

  def retrieveLatLong(search: String): Future[LatLong]

}
