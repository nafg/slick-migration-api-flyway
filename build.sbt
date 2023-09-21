inThisBuild(List(
  crossScalaVersions := Seq("2.12.17", "2.13.10"),
  scalaVersion := crossScalaVersions.value.last,
  organization := "io.github.nafg.slick-migration-api"
))

name := "slick-migration-api-flyway"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.9.0"

libraryDependencies += "org.flywaydb" % "flyway-core" % "9.22.2"

libraryDependencies += "com.h2database" % "h2" % "2.2.224" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"
