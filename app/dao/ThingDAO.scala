package dao

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Thing, ThingRelation}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ThingComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  lazy val things = TableQuery[ThingsTable]
  lazy val relations = TableQuery[ThingRelationsTable]

  class ThingsTable(tag: Tag) extends Table[Thing](tag, "THINGS") {

    def * = (id.?, thingType, name, description, since, until.?) <> (Thing.tupled, Thing.unapply)

    def id = column[Long]("THING_ID", O.PrimaryKey, O.AutoInc)

    def thingType = column[String]("THING_TYPE")

    def name = column[String]("NAME")

    def description = column[String]("DESCRIPTION")

    def since = column[DateTime]("SINCE")

    def until = column[DateTime]("UNTIL")
  }

  class ThingRelationsTable(tag: Tag) extends Table[ThingRelation](tag, "THING_RELATIONS") {

    def * = (id.?, relationType, partId, wholeId, since, until.?, slot, function.?, quantity.?) <> (ThingRelation.tupled, ThingRelation.unapply)

    def id = column[Long]("RELATION_ID", O.PrimaryKey, O.AutoInc)

    def relationType = column[String]("RELATION_TYPE")

    def since = column[DateTime]("SINCE")

    def until = column[DateTime]("UNTIL")

    def slot = column[String]("SLOT")

    def function = column[String]("FUNCTION")

    def quantity = column[Int]("QUANTITY")

    def part = foreignKey("PART", partId, things)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

    def partId = column[Long]("PART_ID")

    def whole = foreignKey("WHOLE", wholeId, things)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

    def wholeId = column[Long]("WHOLE_ID")
  }

}

class ThingDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ThingComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def all(): Future[Seq[Thing]] =
    db.run(things.result)

  def search(query: String): Future[Seq[Thing]] =
    db.run(things.filter(t => (t.name like s"%$query%") || (t.description like s"%$query%")).result)

  def findById(id: Long): Future[Option[Thing]] =
    db.run(things.filter(_.id === id).result.headOption)

  def insert(thing: Thing): Future[Long] =
    db.run((things returning things.map(_.id)) += thing)

  def update(id: Long, thing: Thing): Future[Unit] =
    db.run(things.filter(_.id === id).update(thing.withId(id))).map(_ => ())

  def delete(id: Long): Future[Int] =
    db.run(things.filter(_.id === id).delete)

  def getWholes(id: Long): Future[Seq[(ThingRelation, Thing)]] =
    db.run((relations join things on (_.wholeId === _.id)).filter(_._1.partId === id).result)

  def getParts(id: Long): Future[Seq[(ThingRelation, Thing)]] =
    db.run((relations join things on (_.partId === _.id)).filter(_._1.wholeId === id).result)

  def findRelationById(id: Long): Future[Option[ThingRelation]] =
    db.run(relations.filter(_.id === id).result.headOption)

  def insertRelation(relation: ThingRelation): Future[Long] =
    db.run((relations returning relations.map(_.id)) += relation)

  def updateRelation(id: Long, relation: ThingRelation): Future[Unit] =
    db.run(relations.filter(_.id === id).update(relation)).map(_ => ())

  def deleteRelation(id: Long): Future[Int] =
    db.run(relations.filter(_.id === id).delete)
}
