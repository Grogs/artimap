package server

import java.io.File
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.mapdb.DBMaker
import server.dao.{GeocodingDao, TimeoutDao}
import server.service.MapService
import shared.model.{Entry, LatLong}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration.{FiniteDuration, _}

class Config(private val config: TypesafeConfig = ConfigFactory.load()) extends LazyLogging {
  private val db = DBMaker
    .fileDB(new File("test.db"))
//    .asyncWriteEnable()
//    .executorEnable()
    .closeOnJvmShutdown()
    .make()

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

  private val articleCache: mutable.Map[String,String] = db.hashMap[String, String]("articles").asScala
//  private val geocodingCacheOld: mutable.Map[String, model.LatLong] = db.treeMap[String, model.LatLong]("google-geocoding").asScala
  private val geocodingCache: mutable.Map[String, LatLong] = db.treeMap[String, LatLong]("geocoding-cache").asScala
//  println("start")
//  geocodingCacheOld.foreach{
//    case (key, model.LatLong(lat,long)) =>
//      geocodingCache.put(key, LatLong(lat,long))
//  }
//  println("done")
  private val mapCache: mutable.Map[String, List[(Entry, LatLong)]] = db.treeMap[String, List[(Entry, LatLong)]]("map-service").asScala

  val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(googleKey, geocodingCache)

  val timeoutDao = new TimeoutDao(articleCache)



  val mapService = new MapService(geocodingDao, timeoutDao, mapCache)

}

object Config extends Config(ConfigFactory.load())