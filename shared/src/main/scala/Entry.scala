import scala.concurrent.Future

case class Entry(name: String, link: String, location: String, description: String) {
  def address = s"$name, $location, London, England"
  def getLatLong(implicit dao: GeocodingDaoInter): Future[LatLong] = dao.retrieveLatLong(address)
}

case class LatLong(latitude: Double, longitude: Double)