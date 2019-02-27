package slick.migration.api.flyway

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import slick.dbio.DBIO
import slick.migration.api.{Migration, SqlMigration, TableMigration}

import org.flywaydb.core.api.executor.{Context, MigrationExecutor}
import org.flywaydb.core.api.resolver.ResolvedMigration
import org.flywaydb.core.api.{MigrationType, MigrationVersion}


object VersionedMigration {
  def apply(version: Int, migrations: Migration*): VersionedMigration =
    VersionedMigration(version.toString, migrations: _*)
}

/** Wraps one or more [[slick.migration.api.Migration]] objects with a version string. */
case class VersionedMigration(version: String, migrations: Migration*) extends ResolvedMigration {

  def getDescription: String = {
    migrations.map {
      case m: TableMigration[_, _] => s"TableMigration(${m.tableInfo.tableName})"
      case other                   => other.getClass.getName
    }.mkString("\n")
  }

  def getExecutor: MigrationExecutor = new MigrationExecutor {
    override def canExecuteInTransaction = true

    override def execute(context: Context): Unit = {
      val action = DBIO.sequence(migrations.map(_.apply()))
      val db = new UnmanagedDatabase(context.getConnection)
      Await.result(db.run(action), Duration.Inf)
    }
  }

  def getScript: String = migrations.map {
    case m: TableMigration[_, _] => m.toString
    case m: SqlMigration         => s"SqlMigration{${m.sql.mkString(";")};}"
    case _                       => "<< slick-migration-api >>"
  }.mkString("\n")

  def getChecksum = null

  def getPhysicalLocation: String = "Custom code"

  def getType: MigrationType = MigrationType.CUSTOM

  def getVersion: MigrationVersion = MigrationVersion fromVersion version
}
