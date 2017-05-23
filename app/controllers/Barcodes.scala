package controllers

import dao.{BarcodeDAO, LabelDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.{Barcode, Label}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Barcodes @Inject()(
                        val barcodeDAO: BarcodeDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  val barcodeForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "standard" -> nonEmptyText,
      "code" -> nonEmptyText, //TODO: verify unique
      "thingId" -> longNumber
    )(Barcode.apply)(Barcode.unapply)
  )

  def index = Action.async { implicit request =>
    barcodeDAO.all().map { barcodes => Ok(views.html.barcodes.index(barcodes)) }
  }

  def read(id: Long) = Action.async { implicit request =>
    barcodeDAO.findById(id).map {
        case Some(barcode) => Ok(views.html.barcodes.read(barcode, barcodeForm.fill(barcode)))
        case None => NotFound
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    barcodeForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Barcodes.read(id)).flashing("danger" -> "view.barcode.not_updated"))
      },
      barcode => {
        barcodeDAO.update(id, barcode).map(_ => Redirect(routes.Barcodes.read(id)).flashing("success" -> "view.barcode.updated"))
      }
    )

  }

  def delete(id: Long) = Action.async { implicit request =>
    barcodeDAO.delete(id).map { x: Int =>
      if (x == 1) Redirect(routes.Barcodes.index()).flashing("success" -> "view.barcode.deleted")
      else NotFound
    }
  }

}
