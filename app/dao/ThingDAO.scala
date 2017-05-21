package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.Thing
import models.ThingRelation
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import com.github.tototoshi.slick.MySQLJodaSupport._

trait ThingComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  lazy val things = TableQuery[ThingsTable]

  class ThingsTable(tag: Tag) extends Table[Thing](tag, "THINGS") {

    def id = column[Long]("THING_ID", O.PrimaryKey, O.AutoInc)
    def thingType = column[String]("THING_TYPE")
    def name = column[String]("NAME")
    def description = column[String]("DESCRIPTION")
    def since = column[DateTime]("SINCE")
    def until = column[DateTime]("UNTIL")

    def * = (id.?, thingType, name, description, since, until.?) <> (Thing.tupled, Thing.unapply)
  }

  class ThingRelationsTable(tag: Tag) extends Table[ThingRelation](tag, "THING_RELATIONS") {

    def id = column[Long]("RELATION_ID", O.PrimaryKey, O.AutoInc)
    def relationType = column[String]("RELATION_TYPE")
    def partId = column[Long]("PART_ID")
    def wholeId = column[Long]("WHOLE_ID")
    def since = column[DateTime]("SINCE")
    def until = column[DateTime]("UNTIL")
    def slot = column[String]("SLOT")
    def function = column[String]("FUNCTION")
    def quantity = column[Int]("QUANTITY")

    def * = (id.?, relationType, partId, wholeId, since, until.?, slot, function.?, quantity.?) <> (ThingRelation.tupled, ThingRelation.unapply)

    def part = foreignKey("PART", partId, things)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    def whole = foreignKey("WHOLE", wholeId, things)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
}

class ThingDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ThingComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def all(): Future[Seq[Thing]] =
    db.run(things.result)

  def findById(id: Long): Future[Option[Thing]] =
    db.run(things.filter(_.id === id).result.headOption)

  def insert(thing: Thing): Future[Long] =
    db.run((things returning things.map(_.id)) += thing)

  def update(id: Long, thing: Thing): Future[Unit] =
    db.run(things.filter(_.id === id).update(thing.withId(id))).map(_ => ())

  def delete(id: Long): Future[Int] =
    db.run(things.filter(_.id === id).delete)

}
