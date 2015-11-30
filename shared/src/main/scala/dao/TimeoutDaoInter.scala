package dao

import model.{LatLong, Entry}

trait TimeoutDaoInter {

  def getGeocode(locationId: String): Option[LatLong]

  def getAddress(locationId: String): Option[String]

  def getPage(articleId: String): String

  def getEntries(articleId: String): List[Entry]
}
