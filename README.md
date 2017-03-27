[![Build Status](https://travis-ci.org/101dev/slick-migration-api-flyway.svg?branch=master)](https://travis-ci.org/101dev/slick-migration-api-flyway)

[ ![Download](https://api.bintray.com/packages/1on1development/maven/slick-migration-api-flyway/images/download.svg) ](https://bintray.com/1on1development/maven/slick-migration-api-flyway/_latestVersion)

The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
and the `slick-migration-api` library.

One can aggregate`scala.slick.migration.api.Migration`s into`VersionedMigration`
objects and then pass them to `Flyway` as follows:
```scala
import scala.slick.migration.flyway._

val m1 = TableMigration(testTable)
  .create
  .addColumns(_.col1, _.col2)

val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

val migration = VersionedMigration("1", m1, m2)

val flyway = new Flyway()
flyway.setDataSource(db, "", "")
flyway.setLocations()

flyway.setResolvers(Resolver(migration))

flyway.migrate()
```
(Note that we must use `setLocations()` in order to to avoid searching
for migrations in the default locations)
