
name := """timeout"""

version := "1.0"

val scalaV = "2.11.7"



lazy val scalajsOutputDir = server.base/"src"/"main"/"resources"/"js"

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
      "org.mapdb" % "mapdb" % "2.0-beta12",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scala-lang" % "scala-reflect" % scalaV,
    //      "com.github.julien-truffaut"  %%  "monocle-core"    % "1.2.0-RC1",
//      "com.github.julien-truffaut"  %%  "monocle-macro"   % "1.2.0-RC1",
      //      "com.github.finagle" %% "finch-core" % "0.9.2",
      "com.lihaoyi" %% "pprint" % "0.3.6"
    )//,
//    libraryDependencies += "com.lihaoyi" % "ammonite-repl" % "0.5.1" % "test" cross CrossVersion.full
//    initialCommands in console := """ammonite.repl.Main.run("")"""
  }).settings(Revolver.settings).dependsOn(sharedJvm)

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
      crossTarget in (Compile, packageJSKey) := scalajsOutputDir
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
