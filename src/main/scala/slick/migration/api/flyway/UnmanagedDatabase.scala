package slick.migration.api.flyway

import java.sql.Connection

import slick.jdbc.JdbcBackend.{BaseSession, DatabaseDef}
import slick.jdbc.{JdbcBackend, JdbcDataSource}
import slick.util.AsyncExecutor


class UnmanagedJdbcDataSource(conn: Connection) extends JdbcDataSource {
  override def createConnection() = conn
  override def close(): Unit = ()
  override val maxConnections = None
}

class UnmanagedSession(database: DatabaseDef) extends BaseSession(database) {
  override def close(): Unit = ()
}

class UnmanagedDatabase(conn: Connection)
  extends JdbcBackend.DatabaseDef(new UnmanagedJdbcDataSource(conn), AsyncExecutor("UnmanagedDatabase-AsyncExecutor", 1, -1)) {
  override def createSession() = new UnmanagedSession(this)
}
