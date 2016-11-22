import spray.revolver.RevolverPlugin.Revolver.reStart

organization in Global := "me.gregd.artimap"
version in Global := "1.0"

val scalaV = "2.11.8"

enablePlugins(JavaAppPackaging)

lazy val scalajsOutputDir = server.base / "src" / "main" / "resources" / "js"

lazy val server: sbt.Project = project.settings(
  scalaVersion := scalaV,
  name := "artimap-server",
  version := "1.0",
  libraryDependencies ++= {
    Seq(
      "org.jsoup" % "jsoup" % "1.7.3",
      "org.apache.commons" % "commons-email" % "1.3.3",
      "commons-io" % "commons-io" % "2.4",
      "com.typesafe" % "config" % "1.2.1",
      "com.google.maps" % "google-maps-services" % "0.1.9",
      "com.typesafe.akka" %% "akka-http" % "10.0.0",
      "com.typesafe.akka" %% "akka-actor" % "2.4.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scala-lang" % "scala-reflect" % scalaV,
      "com.lihaoyi" %% "pprint" % "0.3.6",
      "com.lihaoyi" %%% "scalarx" % "0.2.8"

    )
  }).settings(
  javaOptions in reStart += "-DENVIRONMENT=DEV",
  javaOptions in run += "-DENVIRONMENT=PROD",
  test in assembly := {}, //Disable tests during 'sbt assembly'
  javaOptions in assembly += "-DENVIRONMENT=PROD",
  mainClass in assembly := Some("server.Server")
).dependsOn(sharedJvm)

lazy val client: sbt.Project = project.settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  name := "artimap-client",
  version := "1.0",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.1",
    "fr.iscpif" %%% "scaladget" % "0.7.0"
  )
).settings(
  Seq(fastOptJS, fullOptJS) map {
    packageJSKey =>
      crossTarget in(Compile, packageJSKey) := scalajsOutputDir
  }: _*
).enablePlugins(ScalaJSPlugin).dependsOn(sharedJs)


lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  scalaVersion := scalaV,
  name := "artimap-shared",
  version := "1.0",
  libraryDependencies ++= Seq(
    "me.chrons" %%% "boopickle" % "1.2.4",
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "autowire" % "0.2.4",
    "com.lihaoyi" %%% "scalatags" % "0.5.2"
  )
)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

Revolver.settings
