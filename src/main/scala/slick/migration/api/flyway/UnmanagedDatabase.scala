package slick.migration.api.flyway

import java.sql.Connection

import slick.jdbc.JdbcBackend.{BaseSession, DatabaseDef}
import slick.jdbc.{JdbcBackend, JdbcDataSource}
import slick.util.AsyncExecutor


class UnmanagedJdbcDataSource(conn: Connection) extends JdbcDataSource {
  def createConnection() = conn

  def close() = ()
}

class UnmanagedSession(database: DatabaseDef) extends BaseSession(database) {
  override def close() = ()
}

class UnmanagedDatabase(conn: Connection) extends JdbcBackend.DatabaseDef(new UnmanagedJdbcDataSource(conn), AsyncExecutor("UmanagedDatabase-AsyncExecutor", 1, -1)) {
  override def createSession() = new UnmanagedSession(this)
}
