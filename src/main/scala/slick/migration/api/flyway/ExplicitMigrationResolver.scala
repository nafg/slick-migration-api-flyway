package slick.migration.api.flyway

import scala.collection.JavaConverters._
import org.flywaydb.core.api.resolver.MigrationResolver.Context

import org.flywaydb.core.api.resolver.{MigrationResolver, ResolvedMigration}


case class ExplicitMigrationResolver(migrations: ResolvedMigration*) extends MigrationResolver {
  override def resolveMigrations(context: Context) = migrations.asJava
}
