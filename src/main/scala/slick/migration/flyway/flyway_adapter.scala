/** The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
 *  and the `slick-migration-api` library.
 *
 *  One can aggregate [[scala.slick.migration.api.Migration]]s into [[VersionedMigration]]
 *  objects and then pass them to `Flyway` as follows
 *  {{{
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
 *  }}}
 *
 *  (Note that we must use `setLocations()` in order to to avoid searching
 *  for migrations in the default locations)
 */
package scala.slick
package migration.flyway

import java.sql.Connection
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
import scala.slick.migration.api._
import scala.slick.jdbc.UnmanagedSession
import scala.collection.JavaConverters._
import org.flywaydb.core.Flyway

/** Wraps one or more [[scala.slick.migration.api.Migration]] objects with a version string. */
case class VersionedMigration(version: String, migrations: Migration*) extends ResolvedMigration {
  def getChecksum = null

  def getDescription: String = {
    migrations.map {
      case m: TableMigration[_] => s"TableMigration(${m.tableInfo.tableName})"
      case other => other.getClass.getName
    }.mkString("\n")
  }

  def getExecutor: MigrationExecutor = new MigrationExecutor {
    def executeInTransaction = true
    def execute(c: Connection) = migrations foreach (_.apply()(new UnmanagedSession(c)))
  }

  def getPhysicalLocation: String = "Custom code"

  def getScript: String = migrations.map {
    case m: TableMigration[_] => m.toString
    case m: SqlMigration => s"SqlMigration{${m.sql.mkString(";")};}"
    case _ => "<< slick-migration-api >>"
  }.mkString("\n")

  def getType: MigrationType = MigrationType.CUSTOM

  def getVersion: MigrationVersion = MigrationVersion fromVersion version
}

object VersionedMigration {
  def apply(version: Int, migrations: Migration*): VersionedMigration =
    VersionedMigration(version.toString, migrations: _*)
}

object Resolver {
  /** Converts a list of [[ResolvedMigration]]s into a [[MigrationResolver]].
   *  This should be used in conjunction with the [[Flyway#setResolvers]] method.
   */
  def apply(migrations: ResolvedMigration*) = new MigrationResolver {
    def resolveMigrations = migrations.asJavaCollection
  }
  
// TODO can't have these methods due to erasure, consider an implicit conversion instead
//  /** Converts a list of pairs of (version, migrations) into a [[MigrationResolver]].
//   *  This should be used in conjunction with the [[Flyway#setResolvers]] method.
//   */
//  def apply(migrations: (String, Seq[Migration])*): MigrationResolver = {
//    val versioned = migrations.map { case (v, ms) => VersionedMigration(v, ms: _*) }
//    apply(versioned: _*)
//  }
//
//  /** Converts a list of pairs of (version, migrations) into a [[MigrationResolver]].
//   *  This should be used in conjunction with the [[Flyway#setResolvers]] method.
//   */
//  def apply(migrations: (Int, Seq[Migration])*): MigrationResolver = {
//    val versioned = migrations.map { case (v, ms) => VersionedMigration(v, ms: _*) }
//    apply(versioned: _*)
//  }
}
