package shared.dao

import shared.model.{Entry, LatLong}

/**
  * Created by Greg Dorrell on 02/12/2015.
  */
trait TimeoutDaoInter {

  def getGeocode(locationId: String): Option[LatLong]

  def getAddress(locationId: String): Option[String]

  def getPage(articleId: String): List[String]

  def getEntries(articleId: String): List[Entry]
}
