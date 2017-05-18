package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.Barcode
import models.Thing
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class BarcodeDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ThingComponent with HasDatabaseConfigProvider[JdbcProfile] {
    import profile.api._

    private val barcodes = TableQuery[BarcodesTable]

    def all(): Future[Seq[Barcode]] = db.run(barcodes.result)

    def insert(barcode: Barcode): Future[Unit] = db.run(barcodes += barcode).map { _ => () }

    class BarcodesTable(tag: Tag) extends Table[Barcode](tag, "BARCODES") {

      def id = column[Long]("BARCODE_ID", O.PrimaryKey, O.AutoInc)
      def standard = column[String]("STANDARD")
      def code = column[String]("CODE", O.Unique)
      def thingId = column[Long]("THING_ID")
      def * = (id.?, standard, code, thingId) <> (Barcode.tupled, Barcode.unapply)

      def thing = foreignKey("THING", thingId, things)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    }
}
