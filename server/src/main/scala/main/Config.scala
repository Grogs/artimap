package main

import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}
import dao.{GeocodingDao, TimeoutDao}

class Config(private val config: TypesafeConfig = ConfigFactory.load()) {

  private val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(googleKey)

  val timeoutDao = new TimeoutDao()

}

object Config extends Config(ConfigFactory.load())
