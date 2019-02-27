package slick.migration.api.flyway

import java.io.PrintWriter
import java.sql.{DriverManager, SQLException, SQLFeatureNotSupportedException}

import slick.jdbc.JdbcBackend

import javax.sql.DataSource


class DatabaseDatasource(database: JdbcBackend#Database) extends DataSource {
  override def getConnection = database.createSession().conn
  override def getConnection(username: String, password: String) = throw new SQLFeatureNotSupportedException()
  override def unwrap[T](iface: Class[T]) =
    if (iface.isInstance(this)) this.asInstanceOf[T]
    else throw new SQLException(getClass.getName + " is not a wrapper for " + iface)
  override def isWrapperFor(iface: Class[_]) = iface.isInstance(this)
  override def getLogWriter = throw new SQLFeatureNotSupportedException()
  override def setLogWriter(out: PrintWriter): Unit = throw new SQLFeatureNotSupportedException()
  override def setLoginTimeout(seconds: Int): Unit = DriverManager.setLoginTimeout(seconds)
  override def getLoginTimeout = DriverManager.getLoginTimeout
  override def getParentLogger = throw new SQLFeatureNotSupportedException()
}
