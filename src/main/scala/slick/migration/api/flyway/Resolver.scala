package slick.migration.api.flyway

import org.flywaydb.core.api.resolver.ResolvedMigration


object Resolver {
  /** Converts a list of `ResolvedMigration`s into a `MigrationResolver`.
   * Use in conjunction with `org.flywaydb.core.api.configuration.FluentConfiguration#resolvers`.
   */
  @deprecated("Use ExplicitMigrationResolver", "0.6.0")
  def apply(migrations: ResolvedMigration*) = ExplicitMigrationResolver(migrations: _*)
}
