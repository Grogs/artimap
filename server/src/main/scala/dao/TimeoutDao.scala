package dao

import java.io.File

import model.Entry
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup

import scala.collection.JavaConverters._

class TimeoutDao extends TimeoutDaoInter {

  override def getPage(articleId: String) = {
    FileUtils.readFileToString(new File("the-100-best-bars-and-pubs-in-london-full-list.html"))
  }

  override def getEntries(articleId: String, page: String) = {
    val entryHtmls = Jsoup.parse(page).select(".tab__panel > div > div > article").asScala.toList
    for {
      html <- entryHtmls
      aTag = html.select("div.feature-item__content > div > div > h3 > a").first
      link = aTag.attr("href")
      name = aTag.text
      location = html.select("span.listings_flag.icon").text
      description = html.select("p.feature_item__annotation--truncated").text
    } yield Entry(name, link, location, description)
  }

}



