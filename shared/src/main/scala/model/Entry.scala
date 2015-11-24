package model

import dao.GeocodingDaoInter

import scala.concurrent.Future

/**
  * Created by grogs on 24/11/2015.
  */
case class Entry(name: String, link: String, location: String, description: String) {
  def address = s"$name, $location, London, England"
  def getLatLong(implicit dao: GeocodingDaoInter): Future[LatLong] = dao.retrieveLatLong(address)
}
