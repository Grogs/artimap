import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Greg Dorrell on 22/11/2015.
  */
object Main extends App {

  val entries = TimeoutDao.getEntries("")()
  println(entries)

  val first = entries.head
  println(s"\n\nFirst entry: $first\n")
  val latLongF = first.getLatLong(Config.geocodingDao)
  val latLong = Await.result(latLongF, 10.seconds)
  println(s"LatLong: $latLong")

}
