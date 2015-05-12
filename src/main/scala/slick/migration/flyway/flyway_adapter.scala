package scala.slick
package migration

import java.sql.Connection
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
import scala.slick.migration.api._
import scala.slick.jdbc.UnmanagedSession
import scala.collection.JavaConverters._
import org.flywaydb.core.Flyway

/** The `slick.migration.flyway` package is an adapter between the `Flyway` database migration tool,
 *  and the `slick-migration-api` library.
 *  
 *  The [[SlickMigrationFlywayAdapter]] class adds an implicit method that allows
 *  adding [[scala.slick.migration.api.Migration]]s to a `Flyway` object.
 *  An example session might be:
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
 *  flyway.addSlickMigrations(migration1, migration2)
 *
 *  flyway.migrate()
 *  }}}
 * 
 * (Note that we must use `setLocations()` in order to to avoid searching 
 * for migrations in the default locations)
 */
package object flyway {
  /** Provides a method to append [[VersionedMigration]]s to a [[Flyway]] object. */
  implicit class SlickMigrationFlywayAdapter(flyway: Flyway) {
    /** Appends the given [[VersionedMigration]]s to the wrapped [[Flyway]] object.
     *  This invokes the [[Flyway#setResolvers]] method in order to add the migrations
     *  to any existing [[org.flywaydb.core.api.resolver.MigrationResolver]]s.
     *  In case other resolvers are needed, first set them, and only then invoke this
     *  method.
     */
    def addSlickMigrations(migrations: VersionedMigration*): Unit = {
      val resolver = new MigrationResolver {
         def resolveMigrations = migrations.map(versionedMigrationToResolvedMigration).asJavaCollection
      }
      val curResolvers = flyway.getResolvers
      val allResolvers = curResolvers :+ resolver
      
      flyway.setResolvers(allResolvers: _*)
    }

    private def versionedMigrationToResolvedMigration(vm: VersionedMigration): ResolvedMigration =
      new ResolvedMigration {
        def getChecksum = null

        def getDescription: String = {
          vm.migrations.map {
            case m: TableMigration[_] => s"TableMigration(${m.tableInfo.tableName})"
            case other => other.getClass.getName
          }.mkString("\n")
        }

        def getExecutor: MigrationExecutor = new MigrationExecutor {
          def executeInTransaction = true
          def execute(c: Connection) = vm.migrations foreach (_.apply()(new UnmanagedSession(c)))
        }

        def getPhysicalLocation: String = "Custom code"

        def getScript: String = vm.migrations.map {
          case m: TableMigration[_] => m.toString
          case m: SqlMigration => s"SqlMigration{${m.sql.mkString(";")};}"
          case _ => "<< slick-migration-api >>"
        }.mkString("\n")
        
        def getType: MigrationType = MigrationType.CUSTOM
        
        def getVersion: MigrationVersion = MigrationVersion fromVersion vm.version
      }
  }
}

package flyway {
  /** Wraps one or more [[scala.slick.migration.api.Migration]] objects with a version string. */
  case class VersionedMigration(version: String, migrations: Migration*)

  object VersionedMigration {
    def apply(version: Int, migrations: Migration*): VersionedMigration =
      VersionedMigration(version.toString, migrations: _*)
  }
}
