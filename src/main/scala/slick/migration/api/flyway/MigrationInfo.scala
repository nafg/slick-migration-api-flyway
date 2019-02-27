package slick.migration.api.flyway

import java.util.zip.CRC32

import slick.migration.api.{Migration, MigrationSeq, SqlMigration, TableMigration}


case class MigrationInfo(description: String, script: String, checksum: Option[Int], location: String)

object MigrationInfo {
  class Provider[-A <: Migration](val func: A => MigrationInfo)
  object Provider {
    def sql(m: Migration): Seq[String] = m match {
      case MigrationSeq(ms @ _*) => ms.flatMap(sql)
      case sm: SqlMigration      => sm.sql.map(_.linesIterator.mkString("  ").stripSuffix(";") + ";")
      case _                     => m.toString.linesIterator.toSeq.map(" -- " + _)
    }

    def crc32(lines: Iterable[String]) = {
      val crc32 = new CRC32
      for (l <- lines) crc32.update(l.getBytes("UTF-8"))
      crc32.getValue
    }

    /**
     * Should validate okay with migrations that were run from earlier versions of this library
     */
    def compatible: Provider[Migration] =
      new Provider[Migration](migration =>
        MigrationInfo(
          description = migration match {
            case TableMigration(t, _) => s"TableMigration(${t.tableName})"
            case _                    => migration.getClass.getName
          },
          script = sql(migration).mkString("\n"),
          checksum = None,
          location = "Custom code"
        )
      )

    def strict: Provider[Migration] =
      new Provider[Migration]({ migration =>
        val sqlStrings = sql(migration)
        MigrationInfo(
          description = migration.toString,
          script = sqlStrings.mkString("\n"),
          checksum = Some(crc32(sqlStrings).toInt),
          location = migration.getClass.getName
        )
      })
  }
}
