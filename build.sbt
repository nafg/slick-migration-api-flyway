crossScalaVersions := Seq("2.12.8", "2.13.0")
scalaVersion := crossScalaVersions.value.last

organization := "io.github.nafg"

name := "slick-migration-api-flyway"

version := "0.6.1"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.7.0"

libraryDependencies += "org.flywaydb" % "flyway-core" % "6.2.2"

libraryDependencies += "com.h2database" % "h2" % "1.4.200" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"

resolvers += Resolver.jcenterRepo

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/naftoligug/maven/slick-migration-api-flyway")
