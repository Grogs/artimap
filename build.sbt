name := """timeout"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "commons-io" % "commons-io" % "2.4",
  "com.typesafe" % "config" % "1.2.1",
  "com.google.maps" % "google-maps-services" % "0.1.9"
)

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

