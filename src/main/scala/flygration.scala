package flygration
/**
 * flygration package contains class necessary for using flywaydb, a popular database migration tool, with slick-migration-api. Common use-case will create a Flyway object and initialize it with a datasource and then customize some settings.
 * If you are using the migration class files do not setLocations().
 * <code>
 * val f = new Flyway
 * f.setDataSource(Settings.dbUrl, Settings.dbUser, Settings.dbPassword)
 * f.setInitOnMigrate(true)
 * f.setLocations()
 * f.setResolvers(new SMAMigrationResolver(
 *   FWMigration("1", Seq(
 *     TableMigration(table1)
 *       .addColumn(_.col1, "col1")
 *       .addColumn(_.col2, "col2"),
 *     SqlMigration("insert into table1 (col1, col2) values ('data1', 'data2')")
 *   ))
 * f.migrate
 * </code>
 */

import java.sql.Connection
//import java.sql.PreparedStatement
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
//import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
//import org.flywaydb.core.Flyway
import scala.slick.migration.api.Migration
import scala.slick.jdbc.UnmanagedSession
import scala.collection.JavaConverters._
/* 
 * Resolves available migrations
 * This one may work better but Collection is invariant what do I do?
 * Why does it compile now that I changed the original contract?
 * */
class MResolver (fs:FWMigration*) extends MigrationResolver {
  /*
   * Collection<ResolvedMigration> 	resolveMigrations()
   * Resolves the available migrations.
   * ensure this returns a collection. Perhaps each migration tuple may require its own collection
   */
  
  def resolveMigrations: java.util.Collection[ResolvedMigration] = {
    val rms = for (f <- fs) yield new ResolvedM(f.version, f.toString, f.deltas): ResolvedMigration
    rms.asJavaCollection
  }
}

/* Migration resolved through a MigrationResolver. Can be applied against a database.*/
class ResolvedM(version: String, desc: String, deltas: Seq[Migration]) extends ResolvedMigration {
  def getChecksum = null
  def getDescription: String =  desc
  //do I need a separate executer for each migration
  def getExecutor: MigrationExecutor = new MExecuter(deltas: _*) 
  def getPhysicalLocation: String = "Custom code"
  def getScript: String = s"<< slick-migration-api >>"
  def getType: MigrationType = MigrationType.CUSTOM
  def getVersion: MigrationVersion = MigrationVersion fromVersion version
}
/* Executes a migration */
class MExecuter(delta: Migration*) extends MigrationExecutor {
  def executeInTransaction = true
  def execute(c: Connection) = delta foreach (_.apply()(new UnmanagedSession(c)))
}


case class FWMigration(version: String, deltas: Seq[Migration])
