package slick.migration.api.flyway

import scala.collection.JavaConverters._

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.resolver.{Context, MigrationResolver, ResolvedMigration}


object Resolver {
  /** Converts a list of [[ResolvedMigration]]s into a [[MigrationResolver]].
    * This should be used in conjunction with the [[Flyway#setResolvers]] method.
    */
  def apply(migrations: ResolvedMigration*) = new MigrationResolver {
    override def resolveMigrations(context: Context) = migrations.asJava
  }
}
