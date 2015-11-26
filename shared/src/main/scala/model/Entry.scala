package model

import dao.GeocodingDaoInter

import scala.concurrent.Future

case class Entry(name: String, link: String, location: String, description: String) {
  def address = s"$name, $location, London, England"
}
