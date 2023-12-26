package slick.migration.api.flyway

import java.io.PrintWriter
import java.sql.{Connection, DriverManager, SQLException, SQLFeatureNotSupportedException}
import java.util.logging.Logger

import slick.jdbc.JdbcBackend

import javax.sql.DataSource


class DatabaseDatasource(database: JdbcBackend#Database) extends DataSource {
  override def getConnection(): Connection = database.createSession().conn
  override def getConnection(username: String, password: String): Connection = throw new SQLFeatureNotSupportedException()
  override def unwrap[T](iface: Class[T]): T =
    if (iface.isInstance(this)) this.asInstanceOf[T]
    else throw new SQLException(getClass.getName + " is not a wrapper for " + iface)
  override def isWrapperFor(iface: Class[_]): Boolean = iface.isInstance(this)
  override def getLogWriter: PrintWriter = throw new SQLFeatureNotSupportedException()
  override def setLogWriter(out: PrintWriter): Unit = throw new SQLFeatureNotSupportedException()
  override def setLoginTimeout(seconds: Int): Unit = DriverManager.setLoginTimeout(seconds)
  override def getLoginTimeout: Int = DriverManager.getLoginTimeout
  override def getParentLogger: Logger = throw new SQLFeatureNotSupportedException()
}
