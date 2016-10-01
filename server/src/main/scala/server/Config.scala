package server

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import com.typesafe.scalalogging.LazyLogging
import server.dao.{GeocodingDao, TimeoutDao}
import server.service.MapService
import server.util.Persistable
import shared.model.{Entry, LatLong}

import scala.collection.mutable
import scala.concurrent.duration._

import boopickle.Default._

object Environment extends Enumeration {
  type Environment = Value
  val PROD, DEV = Value
}

class Config(private val config: TypesafeConfig = ConfigFactory.load()) extends LazyLogging {

  val environment = Environment withName sys.env.getOrElse("ENVIRONMENT", throw new IllegalArgumentException("Please specify the ENVIRONMENT env variable"))

  val scheduler = new ScheduledThreadPoolExecutor(1)
  def schedule(task: => Unit, frequency: FiniteDuration, initialDelay: Option[FiniteDuration] = None) = scheduler.scheduleAtFixedRate(
    new Runnable { def run() = task },
    initialDelay.getOrElse(frequency).toMillis,
    frequency.toMillis,
    TimeUnit.MILLISECONDS
  )


  private val articleCache = Persistable[mutable.HashMap[String, List[String]]]("articles-cache", mutable.HashMap())
  private val geocodingCache = Persistable[mutable.LinkedHashMap[String, LatLong]]("geocoding-cache", mutable.LinkedHashMap())
  private val mapCache = Persistable[mutable.LinkedHashMap[String, List[(Entry, LatLong)]]]("mapservice-cache", mutable.LinkedHashMap())

  def flushCaches() = {
    logger.debug("Flushing")
    articleCache.flush()
    geocodingCache.flush()
    mapCache.flush()
  }

  schedule(flushCaches(), 5.minutes)


  //Remove invalid (empty) maps
  for{
    (key, entries) <- mapCache.underlying
    if entries.isEmpty
    _ = logger.info(s"Removing invalid/empty map: $key")
  } mapCache.underlying.remove(key)
  for{
    (key, entries) <- articleCache.underlying
    if entries.isEmpty || entries.forall(_.isEmpty)
    _ = logger.info(s"Removing invalid/empty pages: $key")
  } articleCache.underlying.remove(key)
  flushCaches()

//  oldMapCache:    ${oldMapCache.size}
  logger.debug(
    s"""Cache stats:
       |  articleCache:   ${articleCache.underlying.size}
       |  geocodingCache: ${geocodingCache.underlying.size}
       |  mapCache:       ${mapCache.underlying.size}
     """.stripMargin)

  val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(googleKey, geocodingCache.underlying)

  val timeoutDao = new TimeoutDao(articleCache.underlying)

  val mapService = new MapService(geocodingDao, timeoutDao, mapCache.underlying)

}

object Config extends Config(ConfigFactory.parseResources("application.conf"))
