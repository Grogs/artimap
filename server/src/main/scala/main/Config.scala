package main

import java.io.File
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import dao.{GeocodingDao, TimeoutDao}
import model.LatLong
import org.mapdb.DBMaker

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
  private val geocodingCache: mutable.Map[String, LatLong] = db.treeMap[String, LatLong]("google-geocoding").asScala

  val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(googleKey, geocodingCache)

  val timeoutDao = new TimeoutDao(articleCache)

}

object Config extends Config(ConfigFactory.load())
