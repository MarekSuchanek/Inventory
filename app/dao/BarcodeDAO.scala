package dao

import javax.inject.Inject

import models.Barcode
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class BarcodeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ThingComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  lazy val barcodes = TableQuery[BarcodesTable]

  def all(): Future[Seq[Barcode]] =
    db.run(barcodes.result)

  def search(query: String): Future[Seq[Barcode]] =
    db.run(barcodes.filter(_.code like s"%$query%").result)

  def findById(id: Long): Future[Option[Barcode]] =
    db.run(barcodes.filter(_.id === id).result.headOption)

  def findByCode(s: String): Future[Option[Barcode]] =
    db.run(barcodes.filter(_.code === s).result.headOption)

  def insert(barcode: Barcode): Future[Long] =
    db.run((barcodes returning barcodes.map(_.id)) += barcode)

  def delete(id: Long): Future[Int] =
    db.run(barcodes.filter(_.id === id).delete)

  def update(id: Long, barcode: Barcode): Future[Unit] =
    db.run(barcodes.filter(_.id === id).update(barcode.copy(Some(id)))).map(_ => ())

  def getLinkedBarcodes(thingId: Long): Future[Seq[Barcode]] =
    db.run(barcodes.filter(_.thingId === thingId).result)

  class BarcodesTable(tag: Tag) extends Table[Barcode](tag, "BARCODES") {

    def * = (id.?, standard, code, thingId) <> (Barcode.tupled, Barcode.unapply)

    def id = column[Long]("BARCODE_ID", O.PrimaryKey, O.AutoInc)

    def standard = column[String]("STANDARD")

    def code = column[String]("CODE", O.Unique)

    def thing = foreignKey("THING", thingId, things)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

    def thingId = column[Long]("THING_ID")
  }

}
