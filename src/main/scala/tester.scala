import java.sql.Connection
import java.sql.PreparedStatement
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import org.flywaydb.core.Flyway
import scala.slick.migration.api.Migration
import scala.slick.jdbc.UnmanagedSession
import scala.collection.JavaConverters._
import scala.slick.profile.RelationalTableComponent


object tester extends App {

/*
  object table1 extends Table[(Int, Int, Int)]("table1") {
    def col1 = column[Int]("col1")
    def col2 = column[Int]("col2")
    def col3 = column[Int]("col3")
    def * = col1 ~ col2 ~ col3
    }
  implicit val dialect = new H2Dialect


  val migration = TableMigration(table1)
                  .addColumns(_.col1, _.col2)
                  .addColumns(_.col3)

  */                
  val flyway = new Flyway()
  //val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  //flyway.setDataSource(dataSource)
  flyway.migrate()
}