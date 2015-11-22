import com.typesafe.config.ConfigFactory
import com.typesafe.config.{Config=>TypesafeConfig}

class Config(private val config: TypesafeConfig = ConfigFactory.load()) {

  val googleKey = config.getString("api.key.google.maps")

  val geocodingDao = new GeocodingDao(this)

}

object Config extends Config(ConfigFactory.load())
