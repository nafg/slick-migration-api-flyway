inThisBuild(List(
  crossScalaVersions := Seq("2.12.16", "2.13.8"),
  scalaVersion := crossScalaVersions.value.last,
  organization := "io.github.nafg.slick-migration-api"
))

name := "slick-migration-api-flyway"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.8.2"

libraryDependencies += "org.flywaydb" % "flyway-core" % "9.3.0"

libraryDependencies += "com.h2database" % "h2" % "2.1.214" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"
