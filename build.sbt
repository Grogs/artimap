
name := """timeout"""

version := "1.0"

val scalaV = "2.11.7"


lazy val server:sbt.Project = (project in file("server")).settings(
  scalaVersion := scalaV,
  libraryDependencies ++= {
    val akkaV = "2.3.9"
    val sprayV = "1.3.3"
    Seq(
      "org.jsoup" % "jsoup" % "1.7.3",
      "org.apache.commons" % "commons-email" % "1.3.3",
      "commons-io" % "commons-io" % "2.4",
      "com.typesafe" % "config" % "1.2.1",
      "com.google.maps" % "google-maps-services" % "0.1.9",
      "io.spray"            %%  "spray-can"     % sprayV,
      "io.spray"            %%  "spray-routing" % sprayV,
      "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
  }).dependsOn(sharedJvm)

lazy val client:sbt.Project = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.1",
    "fr.iscpif" %%% "scaladget" % "0.7.0"
  )
).settings(
  Seq(fastOptJS, fullOptJS) map {
    packageJSKey =>
      crossTarget in (Compile, packageJSKey) :=
        baseDirectory.value / "../server/resources/"
  }:_*
).enablePlugins(ScalaJSPlugin).dependsOn(sharedJs)


lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(
  scalaVersion := scalaV,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "autowire" % "0.2.4",
    "com.lihaoyi" %%% "scalatags" % "0.5.2"
  )
)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

Revolver.settings
