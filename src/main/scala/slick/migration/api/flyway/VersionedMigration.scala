package slick.migration.api.flyway

import slick.migration.api.Migration

import org.flywaydb.core.api.MigrationType
import org.flywaydb.core.api.executor.MigrationExecutor
import org.flywaydb.core.api.resolver.ResolvedMigration


object VersionedMigration {
  def apply[V: ToMigrationVersion, M <: Migration](version: V, migration: M)
                                                  (implicit infoProvider: MigrationInfo.Provider[M]): VersionedMigration[V] =
    VersionedMigration(version, migration, infoProvider.func(migration))
}

/** Pairs a [[slick.migration.api.Migration]] with a version */
case class VersionedMigration[V](version: V, migration: Migration, info: MigrationInfo)
                                (implicit toMigrationVersion: ToMigrationVersion[V]) extends ResolvedMigration {
  override def getExecutor: MigrationExecutor = new SlickMigrationExecutor(migration)
  override def getDescription: String = info.description
  override def getScript: String = info.script
  override def getChecksum = info.checksum.map(i => i: Integer).orNull
  override def checksumMatches(checksum: Integer) = getChecksum == checksum
  override def checksumMatchesWithoutBeingIdentical(checksum: Integer) = getChecksum == checksum
  override def getPhysicalLocation: String = info.location

  override def getType: MigrationType = MigrationType.CUSTOM

  override def getVersion = toMigrationVersion.func(version)
}
