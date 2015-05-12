package scala.slick
package migration

/** The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
 *  and the `slick-migration-api` library.
 *
 *  One can aggregate [[scala.slick.migration.api.Migration]]s into [[VersionedMigration]]
 *  objects and then pass them to `Flyway` as follows:
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
 *  
 *  The package can also be used to apply general side effects on the database using
 *  `Flyway`. For example:
 *  {{{
 *  val m1: Connection => Unit = c => ...
 *  val m2: Connection => Unit = c => ...
 *  
 *  val migration = VersionedSideEffect.withConnection("1", m1, m2)
 *  }}}
 */
package object flyway // adding an object to have a central place for the package documentation

package flyway {
  import java.sql.Connection
  import org.flywaydb.core.api._
  import org.flywaydb.core.api.resolver._
  import scala.slick.migration.api._
  import scala.slick.jdbc.UnmanagedSession
  import scala.collection.JavaConverters._
  import org.flywaydb.core.Flyway
  import scala.slick.jdbc.JdbcBackend

  /** A skeleton implementation of a custom migration. */
  private[flyway] trait CustomResolvedMigration extends ResolvedMigration {
    def version: String

    def getChecksum = null
    def getPhysicalLocation: String = "Custom code"
    def getType: MigrationType = MigrationType.CUSTOM
    def getVersion: MigrationVersion = MigrationVersion fromVersion version
  }

  /** Wraps one or more [[scala.slick.migration.api.Migration]] objects with a version string. */
  case class VersionedMigration(version: String, migrations: Migration*) extends CustomResolvedMigration {

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

    def getScript: String = migrations.map {
      case m: TableMigration[_] => m.toString
      case m: SqlMigration => s"SqlMigration{${m.sql.mkString(";")};}"
      case _ => "<< slick-migration-api >>"
    }.mkString("\n")
  }

  object VersionedMigration {
    def apply(version: Int, migrations: Migration*): VersionedMigration =
      VersionedMigration(version.toString, migrations: _*)
  }

  /** Wraps a list of side effects on the database as a versioned action.
   *  New instances should be constructed via the companion object.
   */
  class VersionedSideEffect private (val version: String, sideEffects: Seq[(Connection => Unit)]) extends CustomResolvedMigration {
    def getDescription: String = s"general side effect, version $version"

    def getExecutor: MigrationExecutor = new MigrationExecutor {
      def executeInTransaction = true
      def execute(c: Connection) = sideEffects foreach (_(c))
    }

    def getScript: String = getDescription
  }

  object VersionedSideEffect {
    def withConnection(version: String, sideEffects: (Connection => Unit)*) =
      new VersionedSideEffect(version, sideEffects)

    def withSession(version: String, sideEffects: (JdbcBackend#Session => Unit)*) =
      new VersionedSideEffect(version, sideEffects map { f =>
        (c: Connection) => f(new UnmanagedSession(c))
      })
  }

  object Resolver {
    /** Converts a list of [[ResolvedMigration]]s into a [[MigrationResolver]].
     *  This should be used in conjunction with the [[Flyway#setResolvers]] method.
     */
    def apply(migrations: ResolvedMigration*) = new MigrationResolver {
      def resolveMigrations = migrations.asJavaCollection
    }
  }
}