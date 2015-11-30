package dao

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import model.{LatLong, Entry}
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.mutable
import scala.collection.JavaConverters._

class TimeoutDao(cache: mutable.Map[String, String]) extends TimeoutDaoInter with LazyLogging {

  def validate(articleId: String) = {
    val regex = """/([.a-zA-Z0-9\-\_]+/?)+"""
    if (!articleId.matches(s"$regex")) throw new IllegalArgumentException(s"Invalid article ID: $articleId")
  }

  def getGeocode(locationId: String): Option[LatLong] = None

  def getAddress(locationId: String): Option[String] = getAddress(locationId, getPage(locationId))

  override def getEntries(articleId: String) = getEntries(articleId, getPage(articleId))

  def getAddress(locationId: String, page: String): Option[String] = {
      Jsoup.parse(page).select("tr").asScala.find( e =>
        e.children().size() == 2 && e.select("th").size() == 1 && e.select("th").text() == "Address:"
      ).map(_.select("td").html().split("<br( /)?>").map(_.trim).mkString(", "))
  }

  override def getPage(articleId: String) = {
    validate(articleId)
    logger.debug("cached articles: " + cache.size  )
    if (cache.contains(articleId)) {
      logger.debug(s"cache hit: $articleId")
      cache(articleId)
    } else {
      val url = s"http://www.timeout.com$articleId"
      logger.debug(s"Retrieving: $url")
      val page = io.Source.fromURL(url).mkString
      logger.debug(s"Retrieved for $url:\n${page.take(800)}")
      logger.debug(s"populating cache for: $articleId")
      cache.put(articleId, page)
      page
    }
  }


  def getEntries(articleId: String, page: String) = {
    val selectors = List(".tab__panel > div > div > article", ".tiles:first-child > article")
    def select(selector: String): List[Element] = {
      Jsoup.parse(page).select(selector).asScala.toList
    }
    val entryHtmls = selectors.flatMap(select)
    for {
      html <- entryHtmls
      aTag = html.select("div.feature-item__content  h3 > a").first
      link = aTag.attr("href")
      name = aTag.text
      location = html.select("span.listings_flag.icon").text
      description = html.select("p.feature_item__annotation--truncated").text
    } yield Entry(name, link, location, description)
  }

}



