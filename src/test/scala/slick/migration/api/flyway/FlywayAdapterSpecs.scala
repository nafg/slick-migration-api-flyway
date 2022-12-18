package slick.migration.api.flyway

import scala.concurrent.ExecutionContext

import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable
import slick.migration.api.{H2Dialect, Migration, SqlMigration, TableMigration}

import org.flywaydb.core.Flyway
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers


object FlywayAdapterSpecs {
  implicit def infoProvider: MigrationInfo.Provider[Migration] = MigrationInfo.Provider.strict

  // note, not using capital letters in the table/column names breaks the test
  class TestTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE") {
    def col1 = column[Int]("COL1")

    def col2 = column[Int]("COL2")

    def col3 = column[Int]("COL3", O.Default(3))

    def * = (col1, col2, col3)
  }

  val testTable = TableQuery[TestTable]

  implicit val dialect: H2Dialect = new H2Dialect

  case class DBWrap(name: String) {
    val dbAddress = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"
    val database = Database.forURL(dbAddress, driver = "org.h2.Driver")

    def tableExists(implicit executionContext: ExecutionContext) =
      database.run(MTable.getTables(testTable.baseTableRow.tableName).map(_.nonEmpty))

    def tableContents = database.run(testTable.result)
  }

  object slickMigrate {
    val db = DBWrap("slick_migrate")

    val m1 = TableMigration(testTable)
      .create
      .addColumns(_.col1, _.col2)

    val m2 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

    val migration1 = VersionedMigration("1", m1 & m2)

    val m3 = TableMigration(testTable)
      .addColumns(_.col3)

    val m4 = SqlMigration("insert into testtable (col1, col2, col3) values (10, 20, 30)")

    val migration2 = VersionedMigration("2", m3 & m4)

    class Resolver extends ExplicitMigrationResolver(migration1, migration2)
  }

  object slickStepMigrate {
    val db = DBWrap("slick_step_migrate")

    val threeColumns = TableMigration(testTable)
      .create
      .addColumns(_.col1, _.col2, _.col3)
    val dataLine1 = SqlMigration("insert into testtable (col1, col2) values (1, 2)")

    val addThreeColumnsAnd1RowOfData = VersionedMigration("1", threeColumns & dataLine1)

    val dataLine2 = SqlMigration("insert into testtable (col1, col2, col3) values (10, 20, 30)")

    val addAnotherRow = VersionedMigration("2", dataLine2)

    class Resolver1 extends ExplicitMigrationResolver(addThreeColumnsAnd1RowOfData)
    class Resolver2 extends ExplicitMigrationResolver(addThreeColumnsAnd1RowOfData, addAnotherRow)
  }
}
class FlywayAdapterSpecs extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience {

  import ExecutionContext.Implicits.global

  import FlywayAdapterSpecs._


  "The flyway/slick migrations adapter" - {
    "apply slick migrations via a flyway object" in {
      import slickMigrate._
      import db._

      val flyway =
        Flyway.configure()
          .dataSource(dbAddress, "", "")
          .locations(Seq.empty[String]: _*)
          .resolvers(classOf[Resolver].getName)
          .load()

      tableExists.futureValue shouldBe false

      flyway.migrate()

      tableExists.futureValue shouldBe true
      tableContents.futureValue shouldEqual List((1, 2, 3), (10, 20, 30))
    }

    "progressively apply slick migrations via flyway objects" in {
      import slickStepMigrate._
      import db._

      tableExists.futureValue shouldBe false

      val flyway1 =
        Flyway.configure()
          .dataSource(dbAddress, "", "")
          .locations(Seq.empty[String]: _*)
          .resolvers(classOf[Resolver1].getName)
          .load()

      flyway1.migrate()

      tableExists.futureValue shouldBe true
      tableContents.futureValue shouldEqual List((1, 2, 3))

      val flyway2 =
        Flyway.configure()
          .dataSource(dbAddress, "", "")
          .locations(Seq.empty[String]: _*)
          .resolvers(classOf[Resolver2].getName)
          .load()

      flyway2.migrate()

      tableExists.futureValue shouldBe true
      tableContents.futureValue shouldEqual List((1, 2, 3), (10, 20, 30))
    }
  }
}
