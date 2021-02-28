crossScalaVersions := Seq("2.12.13", "2.13.5")
scalaVersion := crossScalaVersions.value.last

organization := "io.github.nafg"

name := "slick-migration-api-flyway"

version := "0.7.0"

scalacOptions += "-deprecation"

libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.8.0"

libraryDependencies += "org.flywaydb" % "flyway-core" % "7.2.1"

libraryDependencies += "com.h2database" % "h2" % "1.4.200" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.4" % "test"

resolvers += Resolver.jcenterRepo

credentials ++= sys.env.get("BINTRAYKEY").toSeq.map(Credentials(
  "Bintray API Realm",
  "api.bintray.com",
  "naftoligug",
  _
))

publishTo := Some("slick-migration-api-flyway @ bintray" at "https://api.bintray.com/maven/naftoligug/maven/slick-migration-api-flyway")
