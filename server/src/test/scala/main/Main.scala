package main

import dao.TimeoutDao

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  val entries = Config.timeoutDao.getEntries("", Config.timeoutDao.getPage(""))
  println(entries)

  val first = entries.head
  println(s"\n\nFirst entry: $first\n")
  val latLongF = Config.geocodingDao.retrieveLatLong(first.address)
  val latLong = Await.result(latLongF, 10.seconds)
  println(s"LatLong: $latLong")

}
