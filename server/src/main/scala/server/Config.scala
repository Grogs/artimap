package server

import java.io.File
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.{Config => TypesafeConfig, ConfigObject, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.mapdb.DBMaker
import server.dao.{GeocodingDao, TimeoutDao}
import server.service.MapService
import shared.model.{Entry, LatLong}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration.{FiniteDuration, _}

object Environment extends Enumeration {
  type Environment = Value
  val PROD, DEV = Value
}

class Config(private val config: TypesafeConfig = ConfigFactory.load()) extends LazyLogging {
  private val dbFile = new File("test.db")
  logger.debug(s"${dbFile.getCanonicalPath} exists: ${dbFile.exists()}")
  private val db = DBMaker
    .fileDB(dbFile)
//    .asyncWriteEnable()
//    .executorEnable()
    .closeOnJvmShutdown()
    .make()

  val environment = Environment withName sys.env.get("ENVIRONMENT").getOrElse(throw new IllegalArgumentException("Please specify the ENVIRONMENT env variable"))

  val scheduler = new ScheduledThreadPoolExecutor(1)
  def schedule(task: => Unit, frequency: FiniteDuration, initialDelay: Option[FiniteDuration] = None) = scheduler.scheduleAtFixedRate(
    new Runnable { def run() = task },
    initialDelay.getOrElse(frequency).toMillis,
    frequency.toMillis,
    TimeUnit.MILLISECONDS
  )

  def flushCaches() = {
    logger.debug("Flushing")
    db.commit()
  }

  schedule(flushCaches(), 5.minutes)

  private val oldArticleCache: mutable.Map[String,String] = db.hashMap[String, String]("articles").asScala
  private val articleCache: mutable.Map[String,List[String]] = db.hashMap[String, List[String]]("articles-cache").asScala
  if (articleCache.isEmpty && !oldArticleCache.isEmpty) oldArticleCache.foreach{ case (key, value) =>
    logger.debug(s"migrating article cache: $key")
    articleCache.put(key, List(value))
  }
  private val geocodingCache: mutable.Map[String, LatLong] = db.treeMap[String, LatLong]("geocoding-cache").asScala
//  private val oldMapCache: mutable.Map[String, List[(Entry, LatLong)]] = db.treeMap[String, List[(Entry, LatLong)]]("map-service").asScala
  private val mapCache: mutable.Map[String, List[(Entry, LatLong)]] = db.treeMap[String, List[(Entry, LatLong)]]("mapservice-cache").asScala

  //Remove invalid (empty) maps
  for{
    (key, entries) <- mapCache
    if entries.isEmpty
    _ = logger.info(s"Removing invalid/empty map: $key")
  } mapCache.remove(key)

//  oldMapCache:    ${oldMapCache.size}
  logger.debug(
    s"""Cache stats:
       |  articleCache:   ${articleCache.size}
       |  geocodingCache: ${geocodingCache.size}
       |  mapCache:       ${mapCache.size}
     """.stripMargin)

  val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(googleKey, geocodingCache)

  val timeoutDao = new TimeoutDao(articleCache)

  val mapService = new MapService(geocodingDao, timeoutDao, mapCache)

}

object Config extends Config(ConfigFactory.parseResources("application.conf"))
