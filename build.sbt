scalaVersion := "2.11.8"

organization := "com.1on1development"

name := "slick-migration-api-flyway"

version := "0.2-SNAPSHOT"

libraryDependencies += "io.github.nafg" %% "slick-migration-api_slick30" % "0.3.0"

libraryDependencies += "org.flywaydb" % "flyway-core" % "4.0.3"

libraryDependencies += "com.h2database" % "h2" % "1.4.192" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

resolvers += "nafg bintray" at "http://dl.bintray.com/naftoligug/maven"

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/1on1development/maven/slick-migration-api-flyway")
