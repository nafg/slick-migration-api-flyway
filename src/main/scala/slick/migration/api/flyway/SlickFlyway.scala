package slick.migration.api.flyway

import slick.jdbc.JdbcBackend

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration


object SlickFlyway {
  def apply(database: JdbcBackend#Database)(migrations: Seq[VersionedMigration]): FluentConfiguration =
    Flyway
      .configure()
      .dataSource(new DatabaseDatasource(database))
      .locations(Seq.empty[String]: _*)
      .resolvers(ExplicitMigrationResolver(migrations: _*))
}
