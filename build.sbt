name := """car_service"""
organization := "com.hevezolly"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.sangria-graphql" %% "sangria" % "3.2.0"
libraryDependencies += "org.sangria-graphql" %% "sangria-circe" % "1.3.2"
libraryDependencies += "org.sangria-graphql" %% "sangria-spray-json" % "1.0.3"
libraryDependencies += "org.sangria-graphql" %% "sangria-play-json" % "2.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.2"
libraryDependencies += "org.postgresql" % "postgresql" % "42.4.2"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.hevezolly.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.hevezolly.binders._"
