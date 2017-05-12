package dao

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import models.Thing
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class ThingDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val Things = TableQuery[ThingsTable]

  def all(): Future[Seq[Thing]] = db.run(Things.result)

  def insert(thing: Thing): Future[Unit] = db.run(Things += thing).map { _ => () }

  private class ThingsTable(tag: Tag) extends Table[Thing](tag, "THING") {

    def id = column[Long]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (id, name) <> (Thing.tupled, Thing.unapply)
  }
}
