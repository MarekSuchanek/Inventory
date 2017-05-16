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

  def all(): Future[Seq[Label]] = db.run(labels.result)

  def insert(label: Label): Future[Unit] = db.run(labels += label).map { _ => () }

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

    def label = foreignKey("LABEL", labelId, labels)(_.id)
    def thing = foreignKey("THING", labelId, things)(_.id)

  }
}
