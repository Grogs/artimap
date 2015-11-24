package dao

import com.google.maps.PendingResult.Callback
import com.google.maps.model.GeocodingResult
import com.google.maps.{GeoApiContext, GeocodingApi}
import main.Config
import model.LatLong

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}


class GeocodingDao(googleKey: String) extends GeocodingDaoInter {

  val context = new GeoApiContext().setApiKey(googleKey)

  type GeoRes = Array[GeocodingResult]

  def retrieveLatLong(search: String): Future[LatLong] = {
    val p = Promise[GeoRes]()
    GeocodingApi.newRequest(context).address(search).setCallback(new Callback[GeoRes] {
      def onResult(res: GeoRes) = p.success(res)
      def onFailure(e: Throwable) = p.failure(e)
    })
    p.future.map(_.head.geometry.location).map(ll => LatLong(ll.lat, ll.lng))
  }

}
