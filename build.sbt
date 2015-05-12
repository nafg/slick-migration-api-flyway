scalaVersion := "2.11.6"

organization := "com.github.101dev"

name := "slick-migration-api-flyway"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.1.1"

libraryDependencies += "org.flywaydb" % "flyway-core" % "3.0"

libraryDependencies += "com.h2database" % "h2" % "1.4.187" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

resolvers += "nafg bintray" at "http://dl.bintray.com/naftoligug/maven"

