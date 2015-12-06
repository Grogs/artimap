package shared.model

case class LatLong(latitude: Double, longitude: Double) {
  def gmapDirectionsLink = s"https://maps.google.com?daddr=$latitude,$longitude"
}
