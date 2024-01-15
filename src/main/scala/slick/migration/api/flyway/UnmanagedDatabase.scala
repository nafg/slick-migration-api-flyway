package slick.migration.api.flyway

import java.sql.Connection

import slick.jdbc.JdbcBackend.BaseSession
import slick.jdbc.{JdbcBackend, JdbcDataSource}
import slick.util.AsyncExecutor


class UnmanagedJdbcDataSource(conn: Connection) extends JdbcDataSource {
  override def createConnection(): Connection = conn
  override def close(): Unit = ()
  override val maxConnections: Option[Int] = None
}

class UnmanagedSession(database: JdbcBackend.Database) extends BaseSession(database) {
  override def close(): Unit = ()
}

class UnmanagedDatabase(conn: Connection)
  extends JdbcBackend.JdbcDatabaseDef(new UnmanagedJdbcDataSource(conn), AsyncExecutor("UnmanagedDatabase-AsyncExecutor", 1, -1)) {
  override def createSession(): UnmanagedSession = new UnmanagedSession(this)
}
