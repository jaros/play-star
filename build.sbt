name := """play-star"""
organization := "ee.profi"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

packageName in Docker := "jaros/play-star"
version in Docker := "latest"
dockerBaseImage := "openjdk"
dockerExposedPorts := Seq(9000)
dockerRepository := Some("docker.io")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ee.profi.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ee.profi.binders._"
