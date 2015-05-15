scalaVersion := "2.11.6"

organization := "com.1on1development"

name := "slick-migration-api-flyway"

version := "0.1"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.1.1"

libraryDependencies += "org.flywaydb" % "flyway-core" % "3.0"

libraryDependencies += "com.h2database" % "h2" % "1.4.187" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

resolvers += "nafg bintray" at "http://dl.bintray.com/naftoligug/maven"

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/1on1development/maven/slick-migration-api-flyway")
