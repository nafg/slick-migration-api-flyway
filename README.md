[![Build Status](https://travis-ci.org/nafg/slick-migration-api-flyway.svg?branch=master)](https://travis-ci.org/101dev/slick-migration-api-flyway)
[ ![Download](https://api.bintray.com/packages/naftoligug/maven/slick-migration-api-flyway/images/download.svg) ](https://bintray.com/naftoligug/maven/slick-migration-api-flyway/_latestVersion)

The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
and the `slick-migration-api` library.

One can aggregate`scala.slick.migration.api.Migration`s into`VersionedMigration`
objects and then pass them to `Flyway` as follows:
```scala
import slick.jdbc.H2Profile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import org.flywaydb.core.Flyway


val db = Database.forConfig("someConfig")

class TestTable(tag: Tag) extends Table[(Int, Int)](tag, "testtable") {
  val col1 = column[Int]("col1")
  val col2 = column[Int]("col2")
  def * = (col1, col2)
}
val testTable = TableQuery[TestTable]

implicit val dialect: H2Dialect = new H2Dialect

val m1 = TableMigration(testTable)
  .create
  .addColumns(_.col1, _.col2)

val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

implicit val infoProvider: MigrationInfo.Provider[Migration] = MigrationInfo.Provider.strict

val migration = VersionedMigration("1", m1 & m2)

val flyway =
  SlickFlyway(db)(Seq(migration))
    .load()

flyway.migrate()
```
