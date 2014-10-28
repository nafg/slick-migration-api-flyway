package somethingElse

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.flywaydb.core.api._
import org.flywaydb.core.api.resolver._
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.flywaydb.core.Flyway
import scala.slick.migration.api.Migration
import scala.slick.jdbc.UnmanagedSession
/*
/**
 * Example of a Java-based migration.
 *
 * public class V1_2__Another_user implements JdbcMigration {
 * public void migrate(Connection connection) throws Exception {
 * PreparedStatement statement =
 * connection.prepareStatement("INSERT INTO test_user (name) VALUES ('Obelix')");
 *
 * try {
 * statement.execute();
 * } finally {
 * statement.close();
 * }
 * }
 * }
  class FWMigration(migrations: Migration*) extends JdbcMigration {
  def migrate(c: java.sql.Connection) {
  println(s"Applying migration ${getClass.getSimpleName}")
  for (migration <- migrations) {
  migration match {
  case sm: SqlMigration =>
  println(s"sql = ${sm.sql}")
  case _ =>
  }
  migration.apply(new ConnectionSession(c))
  }
  }
  }      use flyway.setResolvers
 */

/* Resolves available migrations */
class MResolver(versionName: String, deltas: Migration*) extends MigrationResolver {
  def resolveMigrations: ResolvedMigration  = {
   new ResolvedM(versionName, deltas: _*)
  }
}

/* Migration resolved through a MigrationResolver. Can be applied against a database.*/
class ResolvedM(versionName: String, deltas: Migration*) extends ResolvedMigration {
	def getCheckSum = null
	def getDescription: String = "A random specific unnamed migration"
	def getExecuter: MigrationExecutor = new MExecuter(deltas:_*) //do I need a separate executer for each migration
  def getPhysicalLocation: String = getDescription
  def getScript: String = null
  def getType: MigrationType = MigrationType CUSTOM
  def getVersion: MigrationVersion = MigrationVersion fromVersion versionName
}
/* Executes a migration */
class MExecuter(m: Migration*) extends resolver.MigrationExecutor {
  def executeInTransaction = true
  def execute(c: Connection) = m foreach(_.apply()(new UnmanagedSession(c)))
}

/* A class for instantiating a flyway object for the slick-migration-api */
class sample_flywayDB(flyway: Flyway = new Flyway) {
  val migrations: List[(String, Seq[Migration])]
  /* Adds another database migration to the list of migrations
   * @param versionName the name of this migration
   * @param deltas a sequence of Migration objects representing the deltaas
   */
  def addMigration(versionName: String, deltas: Migration*) = 
    (versionName, deltas:_*) :: migrations

  /* sets resolvers of the flyway object using */ 
  def resolve: Flyway = {
    val resolvers: Seq[MResolver] = 
      for (migration <- migrations) 
        yield  new MResolver(migration._1, migration._2:_*)
    flyway.setResolvers(resolvers:_*)
    flyway
  }
 }
 /* a class for instantiating a flyway object for the slick-migration-api 
 * just addMigrations and resolve!*/
class Flygra(flyway: Flyway = new Flyway) {
  val migrations: Seq[Flygration] = Seq()
  
  /* Adds another database migration to the list of migrations
   * @param version the version name of this migration. 
   * Dots or underscores separate the parts, you can use as many parts as you like
   * @param desc a description of what this migration does.  
   * Underscores or spaces separate the words
   * @param deltas a sequence of Migration objects representing the deltas
   */
   
    
  /* sets resolvers of the flyway object using */
  def flygrate: Flyway = {
//    val resolver: MResolver = new MResolver(migrations)
//      def resolve: Flyway = {
//    val resolvers: Seq[MResolver] =
//      for (migration <- migrations)
//        yield new MResolver(migration._1, migration._2: _*)
    
    flyway.setResolvers(new MResolver(migrations:_*) +: flyway.getResolvers() : _*)
    flyway
  }
}
 
 */
