name := """play-star"""
organization := "ee.profi"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3"

herokuAppName in Compile := "dry-spire-89439"
packageName in Docker := "jaros/play-star"
version in Docker := "latest"
dockerExposedPorts := Seq(9000)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ee.profi.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ee.profi.binders._"
