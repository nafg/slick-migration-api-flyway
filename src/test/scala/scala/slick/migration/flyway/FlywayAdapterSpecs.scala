package scala.slick
package migration.flyway

import org.scalatest.{ FreeSpec, Matchers }
import scala.slick.driver.H2Driver.simple._
import scala.slick.migration.api._
import org.flywaydb.core.Flyway
import scala.slick.jdbc.meta.MTable

class FlywayAdapterSpecs extends FreeSpec with Matchers {
  // note, not using capital letters in the table/column names breaks the test
  class TestTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE") {
    def col1 = column[Int]("COL1")
    def col2 = column[Int]("COL2")
    def col3 = column[Int]("COL3", O.Default(3))
    def * = (col1, col2, col3)
  }

  val testTable = TableQuery[TestTable]

  implicit val dialect = new H2Dialect
  val dbAddress = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1"
  val db = Database.forURL(dbAddress, driver = "org.h2.Driver")

  def tableExists() = db withSession { implicit s =>
    !MTable.getTables(testTable.baseTableRow.tableName).list.isEmpty
  }

  def tableContents() = db withSession {
    implicit s => testTable.list
  }

  "The flyway/slick migrations adapter" - {
    "apply slick migrations via a flyway object" in {
      val m1 = TableMigration(testTable)
        .create
        .addColumns(_.col1, _.col2)

      val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

      val migration1 = VersionedMigration("1", m1, m2)

      val m3 = TableMigration(testTable)
        .addColumns(_.col3)

      val m4 = SqlMigration("insert into testtable (col1, col2, col3) values (10, 20, 30)")

      val migration2 = VersionedMigration("2", m3, m4)

      val flyway = new Flyway()
      flyway.setDataSource(dbAddress, "", "")
      flyway.setLocations()

      flyway.setResolvers(Resolver(migration1, migration2))

      tableExists() shouldBe false
      
      flyway.migrate()

      tableExists() shouldBe true
      tableContents() shouldEqual List((1, 2, 3), (10, 20, 30))
    }
  }
}