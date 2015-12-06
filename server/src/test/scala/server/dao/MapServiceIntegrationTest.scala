package server.dao

import org.scalatest.{MustMatchers, FunSuite}
import server.Config

class MapServiceIntegrationTest extends FunSuite with MustMatchers {
  test("bristol coffee") {
    val entries = Config.timeoutDao.getEntries("/london/food-and-drink/londons-best-burger-restaurants-1")
    println(entries)
    entries.size mustEqual 15
  }

  test("london's best burger restaurants") {
    val entries = Config.timeoutDao.getEntries("/london/food-and-drink/londons-best-burger-restaurants-1")
    println(entries)
    entries.size mustEqual 15
  }

}
