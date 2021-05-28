inThisBuild(List(
  crossScalaVersions := Seq("2.12.14", "2.13.6"),
  scalaVersion := crossScalaVersions.value.last,
  organization := "io.github.nafg.slick-migration-api"
))

name := "slick-migration-api-flyway"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.8.2"

libraryDependencies += "org.flywaydb" % "flyway-core" % "7.9.1"

libraryDependencies += "com.h2database" % "h2" % "1.4.200" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test"
