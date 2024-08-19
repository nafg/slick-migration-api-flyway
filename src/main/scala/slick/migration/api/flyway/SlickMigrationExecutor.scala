package slick.migration.api.flyway

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import slick.migration.api.Migration

import org.flywaydb.core.api.executor.{Context, MigrationExecutor}


class SlickMigrationExecutor(migration: Migration) extends MigrationExecutor {
  override def shouldExecute(): Boolean = true

  override def canExecuteInTransaction = true

  override def execute(context: Context) = {
    val db = new UnmanagedDatabase(context.getConnection)
    Await.result(db.run(migration()), Duration.Inf)
    java.util.List.of()
  }
}
