package shared.service
import shared.model.{LatLong, Entry}

import scala.concurrent.Future

trait MapServiceInter {

  def getPositionedEntries(article: String): Future[List[(Entry, LatLong)]]

}
