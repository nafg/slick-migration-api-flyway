package flygration

import org.flywaydb.core.Flyway
import scala.slick.driver.H2Driver.simple._
import scala.slick.migration.api._
import flygration._
import scala.slick.jdbc.meta.MTable
import org.flywaydb.core.internal.util.jdbc.DriverDataSource

/** An example usage of the `flygration` package. */
object Example extends App {
  class TestTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE") {
    def col1 = column[Int]("COL1")
    def col2 = column[Int]("COL2")
    def col3 = column[Int]("COL3", O.Default(3))
    def * = (col1, col2, col3)
  }

  val testTable = TableQuery[TestTable]

  implicit val dialect = new H2Dialect

  val m1 = TableMigration(testTable)
    .create
    .addColumns(_.col1, _.col2)

  val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

  val migration1 = VersionedMigration("1", m1, m2)

  val m3 = TableMigration(testTable)
    .addColumns(_.col3)

  val m4 = SqlMigration("insert into testtable (col1, col2, col3) values (10, 20, 30)")

  val migration2 = VersionedMigration("2", m3, m4)

  val db = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1"
  
  val flyway = new Flyway()
  flyway.setDataSource(db, "", "")
  flyway.setLocations()
  
  flyway.addSlickMigrations(migration1, migration2)
  
  flyway.migrate()

  Database.forURL(db, driver = "org.h2.Driver") withSession {
    implicit s =>
      assert(testTable.list == List((1, 2, 3), (10, 20, 30)))
      
      testTable foreach println
  }

}