scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8", "2.12.1")

organization := "com.1on1development"

name := "slick-migration-api-flyway"

version := "0.4"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.4.0"

libraryDependencies += "org.flywaydb" % "flyway-core" % "4.1.2"

libraryDependencies += "com.h2database" % "h2" % "1.4.194" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

resolvers += "nafg bintray" at "http://dl.bintray.com/naftoligug/maven"

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/1on1development/maven/slick-migration-api-flyway")
