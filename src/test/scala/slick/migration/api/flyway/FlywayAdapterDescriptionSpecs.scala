package slick.migration.api.flyway

import scala.concurrent.ExecutionContext

import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable
import slick.migration.api.{H2Dialect, Migration, SqlMigration, TableMigration}

import org.flywaydb.core.Flyway
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FreeSpec, Matchers}

class FlywayAdapterDescriptionSpecs extends FreeSpec with Matchers with ScalaFutures with IntegrationPatience {
  implicit def infoProvider: MigrationInfo.Provider[Migration] = MigrationInfo.Provider.strict

  // note, not using capital letters in the table/column names breaks the test
  class TestTable1(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE1") {
    def col1 = column[Int]("COLUMN1")
    def col2 = column[Int]("COLUMN2")
    def col3 = column[Int]("COLUMN3", O.Default(3))

    def * = (col1, col2, col3)
  }

  class TestTable2(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE2") {
    def col1 = column[Int]("COLUMN1")
    def col2 = column[Int]("COLUMN2")
    def col3 = column[Int]("COLUMN3", O.Default(3))

    def * = (col1, col2, col3)
  }

  class TestTable3(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE3") {
    def col1 = column[Int]("COLUMN1")
    def col2 = column[Int]("COLUMN2")
    def col3 = column[Int]("COLUMN3", O.Default(3))

    def * = (col1, col2, col3)
  }

  class TestTable4(tag: Tag) extends Table[(Int, Int, Int)](tag, "TESTTABLE4") {
    def col1 = column[Int]("COLUMN1")
    def col2 = column[Int]("COLUMN2")
    def col3 = column[Int]("COLUMN3", O.Default(3))

    def * = (col1, col2, col3)
  }

  implicit val dialect: H2Dialect = new H2Dialect

  case class DBWrap(name: String) {
    val dbAddress = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"
    val database = Database.forURL(dbAddress, driver = "org.h2.Driver")

    val testTable1 = TableQuery[TestTable1]
    def tableExists(implicit executionContext: ExecutionContext) =
      database.run(MTable.getTables(testTable1.baseTableRow.tableName).map(_.nonEmpty))

    def tableContents = database.run(testTable1.result)
  }

  import ExecutionContext.Implicits.global

  "The flyway/slick migrations adapter" - {

    "Run the migration for the second time" in {
      //The migration of 2 tables ensures that the description is < 200 characters.
      //Failure is due to memory references of the description that uses 'migrate.toString'
      migrate2Tables(true)

      migrate2Tables()
    }

    "Run the migration for the second time with a lengthy generated description" in {
      //The migration of 4 tables will create a description > 200 characters and fails
      //because the comparison takes the original lenght and not the 200 char lenght that has the last
      //3 characters replaced with '...'
      //NOTE: at this moment, the test fails due to the memory references too.
      migrate4TablesLenghtyDescription(true)

      migrate4TablesLenghtyDescription()
    }

  }


  private def migrate2Tables(firstTime: Boolean = false): Unit = {
    val db = DBWrap("slick_migrate_2tables")
    import db._

    val m1 = TableMigration(TableQuery[TestTable1])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val m2 = TableMigration(TableQuery[TestTable2])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val migration1 = VersionedMigration("1", m1 & m2)
    val flyway =
      Flyway.configure()
        .dataSource(dbAddress, "", "")
        .locations(Seq.empty[String]: _*)
        .resolvers(ExplicitMigrationResolver(migration1))
        .load()

    if (firstTime) tableExists.futureValue shouldBe false else true

    flyway.migrate()

    tableExists.futureValue shouldBe true

  }

  private def migrate4TablesLenghtyDescription(firstTime: Boolean = false): Unit = {
    val db = DBWrap("slick_migrate_4tables")
    import db._

    val m1 = TableMigration(TableQuery[TestTable1])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val m2 = TableMigration(TableQuery[TestTable2])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val m3 = TableMigration(TableQuery[TestTable3])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val m4 = TableMigration(TableQuery[TestTable4])
      .create
      .addColumns(_.col1, _.col2, _.col3)

    val migration1 = VersionedMigration("1", m1 & m2 & m3 & m4)
    val flyway =
      Flyway.configure()
        .dataSource(dbAddress, "", "")
        .locations(Seq.empty[String]: _*)
        .resolvers(ExplicitMigrationResolver(migration1))
        .load()

    if (firstTime) tableExists.futureValue shouldBe false else true

    flyway.migrate()

    tableExists.futureValue shouldBe true

  }

}
