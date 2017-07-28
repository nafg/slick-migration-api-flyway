scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.11", "2.12.3")

organization := "com.1on1development"

name := "slick-migration-api-flyway"

version := "0.4.1"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.4.1"

libraryDependencies += "org.flywaydb" % "flyway-core" % "4.2.0"

libraryDependencies += "com.h2database" % "h2" % "1.4.196" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"

resolvers += Resolver.jcenterRepo

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/1on1development/maven/slick-migration-api-flyway")
