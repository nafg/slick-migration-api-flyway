inThisBuild(List(
  crossScalaVersions := Seq("3.3.7", "2.13.17"),
  scalaVersion := crossScalaVersions.value.last,
  organization := "io.github.nafg.slick-migration-api"
))

name := "slick-migration-api-flyway"

scalacOptions ++= Seq("-feature", "-deprecation", "-Xsource:3")

libraryDependencies += "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.11.1"

libraryDependencies += "org.flywaydb" % "flyway-core" % "11.14.0"

libraryDependencies += "com.h2database" % "h2" % "2.4.240" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
