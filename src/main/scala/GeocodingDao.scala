import com.google.maps.PendingResult.Callback
import com.google.maps.model.{LatLng, GeocodingResult}
import com.google.maps.{GeoApiContext, GeocodingApi}

import scala.concurrent,concurrent.{Future, Promise},concurrent.ExecutionContext.Implicits.global


/**
  * Created by Greg Dorrell on 22/11/2015.
  */
class GeocodingDao(config: Config) {

  val context = new GeoApiContext().setApiKey(config.googleKey)

  type GeoRes = Array[GeocodingResult]

  def retrieveLatLong(search: String): Future[LatLng] = {
    val p = Promise[GeoRes]()
    GeocodingApi.newRequest(context).address(search).setCallback(new Callback[GeoRes] {
      def onResult(res: GeoRes) = p.success(res)
      def onFailure(e: Throwable) = p.failure(e)
    })
    p.future.map(_.head.geometry.location)
  }

}

object GeocodingDao extends GeocodingDao(Config)
