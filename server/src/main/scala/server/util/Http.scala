package server.util

import java.net.{HttpURLConnection, URL}

import org.apache.commons.io.IOUtils

/**
  * Created by grogs on 23/11/2015.
  */
object Http {

  object logger {
    def info(msg: String) = println(msg)
    def info(msg: String, e:Throwable) = {
      println(msg)
      e.printStackTrace()
    }
  }
  /**
    * download with Fake user agent
    */
  def curl(url: String,
           method: Option[String] = None,
           cookie: Option[String] = None,
           referrer: Option[String] = None,
           otherParams: Map[String, String] = Map.empty) = {
    logger.info(s"downloading from url $url\n with method = $method, cookie = $cookie, referrer = $referrer")

    val conn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)") // fake firefox
    conn.setRequestProperty("Accept-Language", "en-US")  // make sure retrieved currency is USD

    method.foreach { conn.setRequestMethod }
    cookie.foreach { conn.setRequestProperty("Cookie", _) }
    referrer.foreach { conn.setRequestProperty("Referer", _) }
    otherParams.foreach {
      case (key, value) => conn.setRequestProperty(key, value)
    }

    logger.info(s"dispatching request with properties ${conn.getRequestProperties}")

    val is = conn.getInputStream
    try {
      IOUtils.toString(is)
    } catch {
      case e: Throwable =>
        logger.info(s"unable to download from $url", e)
        throw e
    } finally {
      is.close()
    }
  }

}
