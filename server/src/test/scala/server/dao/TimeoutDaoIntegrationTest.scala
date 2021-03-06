package server.dao

import org.scalatest.{MustMatchers, FunSuite}
import server.Config

class TimeoutDaoIntegrationTest extends FunSuite with MustMatchers {
//  test("Ice skating rinks in London") {
//    val entries = Config.timeoutDao.getEntries("/london/things-to-do/ice-skating-in-london")
//    println(entries)
//    entries.size mustEqual 12
//  }
  test("bristol coffee") {
    val entries = Config.timeoutDao.getEntries("/bristol/restaurants/the-best-coffee-shops-and-cafes-in-bristol")
    println(entries)
  entries.size mustEqual 21
  }
}
