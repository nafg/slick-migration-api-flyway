package slick.migration.api

import slick.migration.api.flyway.VersionedMigration


/** The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
  * and the `slick-migration-api` library.
  *
  * One can aggregate `slick.migration.api.Migrations` into [[VersionedMigration]]
  * objects and then pass them to `Flyway` as follows:
  * {{{
  *  import scala.slick.migration.flyway._
  *
  *  val m1 = TableMigration(testTable)
  *    .create
  *    .addColumns(_.col1, _.col2)
  *
  *  val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")
  *
  *  val migration = VersionedMigration("1", m1, m2)
  *
  *  val flyway = SlickFlyway(database)(Seq(migration)).load()
  *
  *  flyway.migrate()
  * }}}
  *
  * (Note that we must use `setLocations()` in order to to avoid searching
  * for migrations in the default locations)
  *
  */
package object flyway
