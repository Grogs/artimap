package server.service

import shared.dao.{TimeoutDaoInter, GeocodingDaoInter}
import shared.model.{LatLong, Entry}
import shared.service.MapServiceInter
import scala.collection.mutable
import scala.concurrent.Future
import server.util.MutableMapPimp
import scala.concurrent.ExecutionContext.Implicits.global

class MapService(geocodingDao: GeocodingDaoInter, timeoutDao: TimeoutDaoInter, cache: mutable.Map[String, List[(Entry, LatLong)]]) extends MapServiceInter {

  private def getPosition(entry: Entry): Future[LatLong] = {
    timeoutDao.getGeocode(entry.relPath) match {
      case Some(validPos) => Future.successful(validPos)
      case None =>
        val address = timeoutDao.getAddress(entry.relPath)
        geocodingDao.retrieveLatLong(address getOrElse entry.address)
    }
  }

  def getPositionedEntries(article: String): Future[List[(Entry, LatLong)]] = {
    cache.getOrElseUpdateAsync(article, {
      val entries = timeoutDao.getEntries(article)
      val res = Future.sequence(for {
        entry <- entries
      } yield for {
        pos <- getPosition(entry)
        res = entry -> pos
      } yield res)
      res
    })

  }
}
