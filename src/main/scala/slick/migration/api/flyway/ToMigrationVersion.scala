package slick.migration.api.flyway

import org.flywaydb.core.api.MigrationVersion


class ToMigrationVersion[A](val func: A => MigrationVersion)

object ToMigrationVersion {
  implicit val migrationVersion: ToMigrationVersion[MigrationVersion] =
    new ToMigrationVersion[MigrationVersion](identity)
  implicit val string: ToMigrationVersion[String] = new ToMigrationVersion[String](MigrationVersion.fromVersion)
  implicit val int: ToMigrationVersion[Int] = new ToMigrationVersion[Int](i => MigrationVersion.fromVersion(i.toString))
}
