package server.service

import java.io.FileNotFoundException

import com.typesafe.scalalogging.LazyLogging
import shared.dao.{TimeoutDaoInter, GeocodingDaoInter}
import shared.model.{LatLong, Entry}
import shared.service.MapServiceInter
import scala.collection.mutable
import scala.concurrent.Future
import server.util.MutableMapPimp
import scala.concurrent.ExecutionContext.Implicits.global

class MapService(geocodingDao: GeocodingDaoInter, timeoutDao: TimeoutDaoInter, cache: mutable.Map[String, List[(Entry, LatLong)]]) extends MapServiceInter with LazyLogging {

  private def getPosition(entry: Entry): Future[LatLong] = {
    Future(timeoutDao.getGeocode(entry.relPath)).flatMap(_ match {
      case Some(validPos) => Future.successful(validPos)
      case None =>
        val address = timeoutDao.getAddress(entry.relPath)
        geocodingDao.retrieveLatLong(address getOrElse entry.address)
    })
  }

  def remove404s(positions: List[Future[(Entry, LatLong)]]) = positions.map(_.map(Option(_)).recover{
    case error404: FileNotFoundException =>
      logger.error("received 404 when retrieving position, skipping", error404)
      None
  })

  def getPositionedEntries(article: String): Future[List[(Entry, LatLong)]] = {
    cache.getOrElseUpdateAsync(article, {
      val entries = timeoutDao.getEntries(article)
      val res = Future.sequence(remove404s(for {
        entry <- entries
      } yield for {
        pos <- getPosition(entry)
        res = entry -> pos
      } yield res)).map(_.flatten)
      res
    })

  }
}
