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
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
import scala.slick.migration.api._
import scala.slick.jdbc.UnmanagedSession
import scala.collection.JavaConverters._
/* 
 * Resolves available migrations
 * */
class SMAMigrationResolver (ms:FWMigration*) extends MigrationResolver {
  /*
   * Resolves the available migrations.
   */
  def resolveMigrations: java.util.Collection[ResolvedMigration] = {
    val rms = for (m <- ms) yield new SMAResolvedMigration(m): ResolvedMigration
    rms.asJavaCollection
  }
}

/* Migration resolved through a MigrationResolver. Can be applied against a database.*/
class SMAResolvedMigration(m: FWMigration) extends ResolvedMigration {
  def getChecksum = null
  def getDescription: String =  m.desc
  def getExecutor: MigrationExecutor = new SMAMigrationExecutor(m.deltas: _*)
  def getPhysicalLocation: String = "Custom code"
  def getScript: String = m.deltas.map{
    case m: TableMigration[_] => m.toString
    case m: SqlMigration   => s"SqlMigration{${m.sql.mkString(";")};}"
    case _                 => "<< slick-migration-api >>"
  }.mkString("\n")
  def getType: MigrationType = MigrationType.CUSTOM
  def getVersion: MigrationVersion = MigrationVersion fromVersion m.version
}
/* Executes a migration */
class SMAMigrationExecutor(delta: Migration*) extends MigrationExecutor {
  def executeInTransaction = true
  def execute(c: Connection) = delta foreach (_.apply()(new UnmanagedSession(c)))
}

case class FWMigration(version: String, desc: String, deltas: Seq[Migration])

object FWMigration {
  def apply(version: String, deltas: Seq[Migration]): FWMigration = {
    val desc = deltas.map{
      case m: TableMigration[_] => s"TableMigration(${m.tableInfo.tableName})"
      case other => other.getClass.getName
    }.mkString("\n")
    FWMigration(version, desc, deltas)
  }
}
