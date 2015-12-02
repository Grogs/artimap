package server.dao

import com.google.maps.PendingResult.Callback
import com.google.maps.model.GeocodingResult
import com.google.maps.{GeoApiContext, GeocodingApi}
import com.typesafe.scalalogging.LazyLogging
import shared.dao.GeocodingDaoInter
import shared.model.LatLong

import scala.collection.mutable
import scala.concurrent.{Future, Promise}


class GeocodingDao(googleKey: String, cache: mutable.Map[String, LatLong]) extends GeocodingDaoInter with LazyLogging {

  val context = new GeoApiContext().setApiKey(googleKey)

  type GeoRes = Array[GeocodingResult]

  def retrieveLatLong(search: String): Future[LatLong] = {
    val p = Promise[LatLong]()
    logger.debug("cached addresses: " + cache.size)
    cache.get(search) match {
      case Some(res) =>
        logger.debug(s"cache hit: $search")
        p.success(res)
      case None => GeocodingApi.newRequest(context).address(search).setCallback(new Callback[GeoRes] {
        def onResult(response: GeoRes) = {
          val latLng = response.head.geometry.location
          val result = LatLong(latLng.lat, latLng.lng)
          logger.debug(s"populating cache for: $search")
          cache.put(search, result)
          p.success(result)
        }
        def onFailure(e: Throwable) = {
          logger.error(s"Failed to retrieve LatLong! for: $search", e)
          p.failure(e)
        }
      })
    }
    p.future
  }

}
