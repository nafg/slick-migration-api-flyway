package scala.slick
package migration

import scala.slick.jdbc.JdbcBackend
import scala.slick.migration.api._


/** The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
  * and the `slick-migration-api` library.
  *
  * One can aggregate [[scala.slick.migration.api.Migration]]s into [[scala.slick.migration.flyway.VersionedMigration]]
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
  *  val flyway = new Flyway()
  *  flyway.setDataSource(db, "", "")
  *  flyway.setLocations()
  *
  *  flyway.setResolvers(Resolver(migration))
  *
  *  flyway.migrate()
  * }}}
  *
  * (Note that we must use `setLocations()` in order to to avoid searching
  * for migrations in the default locations)
  *
  * It is possible to convert general side effecting actions into a migration,
  * for example:
  * {{{
  *  import scala.slick.migration.flyway._
  *  val m: Migration = sideEffect { implicit session => ... }
  * }}}
  */
package object flyway {
  /** Converts a side effecting action that requires a session into a migration. */
  def sideEffect(eff: => (JdbcBackend#Session => Unit)) = new Migration {
    def apply()(implicit session: JdbcBackend#Session) = {
      eff(session)
    }
  }
}
