inThisBuild(List(
  crossScalaVersions := Seq("2.12.15", "2.13.7"),
  scalaVersion := crossScalaVersions.value.last,
  organization := "io.github.nafg.slick-migration-api"
))

name := "slick-migration-api-flyway"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.8.2"

libraryDependencies += "org.flywaydb" % "flyway-core" % "8.1.0"

libraryDependencies += "com.h2database" % "h2" % "2.0.202" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"
