package slick.migration.api.flyway

import scala.collection.JavaConverters._

import org.flywaydb.core.api.resolver.{Context, MigrationResolver, ResolvedMigration}


case class ExplicitMigrationResolver(migrations: ResolvedMigration*) extends MigrationResolver {
  override def resolveMigrations(context: Context) = migrations.asJava
}
