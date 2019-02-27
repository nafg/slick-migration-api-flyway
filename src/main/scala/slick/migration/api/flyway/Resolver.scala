package slick.migration.api.flyway

import org.flywaydb.core.api.resolver.{MigrationResolver, ResolvedMigration}


object Resolver {
  /** Converts a list of [[ResolvedMigration]]s into a [[MigrationResolver]].
   * This should be used in conjunction with the [[org.flywaydb.core.api.configuration.FluentConfiguration#resolvers]] method.
   */
  @deprecated("Use ExplicitMigrationResolver", "0.6.0")
  def apply(migrations: ResolvedMigration*) = ExplicitMigrationResolver(migrations: _*)
}
