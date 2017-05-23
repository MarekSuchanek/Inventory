package dao

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import models.Label
import models.LabelThing
import models.Thing

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class LabelDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ThingComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  lazy val labels = TableQuery[LabelsTable]

  lazy val labelThings = TableQuery[LabelThingsTable]

  def all(): Future[Seq[Label]] =
    db.run(labels.result)

  def search(query: String): Future[Seq[Label]] =
    db.run(labels.filter(l => (l.name like s"%$query%") || (l.description like s"%$query%")).result)

  def findById(id: Long): Future[Option[Label]] =
    db.run(labels.filter(_.id === id).result.headOption)

  def insert(label: Label): Future[Long] =
    db.run((labels returning labels.map(_.id)) += label)

  def update(id: Long, label: Label): Future[Unit] =
    db.run(labels.filter(_.id === id).update(label.copy(Some(id)))).map(_ => ())

  def delete(id: Long): Future[Int] =
    db.run(labels.filter(_.id === id).delete)

  def getLinkedLabels(thingId: Long): Future[Seq[(LabelThing, Label)]] =
    db.run((labelThings join labels on (_.labelId === _.id)).filter(_._1.thingId === thingId).result)

  def getLinkedThings(labelId: Long): Future[Seq[(LabelThing, Thing)]] =
    db.run((labelThings join things on (_.thingId === _.id)).filter(_._1.labelId === labelId).result)

  def linkLabel(labelThing: LabelThing): Future[Long] =
    db.run((labelThings returning labelThings.map(_.id)) += labelThing)

  def unlinkLabel(id: Long): Future[Int] =
    db.run(labelThings.filter(_.id === id).delete)

  class LabelsTable(tag: Tag) extends Table[Label](tag, "LABELS") {

    def id = column[Long]("LABEL_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def description = column[String]("DESCRIPTION")
    def color = column[String]("COLOR")
    def * = (id.?, name, description, color) <> (Label.tupled, Label.unapply)
  }

  class LabelThingsTable(tag: Tag) extends Table[LabelThing](tag, "LABELS_THINGS") {

    def id = column[Long]("LABELTHING_ID", O.PrimaryKey, O.AutoInc)
    def labelId = column[Long]("LABEL_ID")
    def thingId = column[Long]("THING_ID")
    def * = (id.?, labelId, thingId) <> (LabelThing.tupled, LabelThing.unapply)

    def label = foreignKey("LABEL", labelId, labels)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    def thing = foreignKey("THING", labelId, things)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

  }
}
