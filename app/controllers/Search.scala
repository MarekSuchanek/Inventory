package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class Search @Inject()(
                        val thingDAO: ThingDAO,
                        val barcodeDAO: BarcodeDAO,
                        val labelDAO: LabelDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  def search(query: Option[String]) = Action.async { implicit request =>
    query match {
      case None => Future.successful(Ok(views.html.search.search(query)))
      case Some(s) =>
        lazy val data = for {
          things <- thingDAO.search(s)
          labels <- labelDAO.search(s)
          barcodes <- barcodeDAO.search(s)
        } yield (things, labels, barcodes)

        data.map { case (things, labels, barcodes) =>
          Ok(views.html.search.search(query, things, labels, barcodes))
        }
    }
  }

  def searchRedir = Action { implicit request =>
    Redirect(routes.Search.search(Some(request.body.asFormUrlEncoded.get("query").head)))
  }

  def barcode(query: Option[String]) = Action.async { implicit request =>
    query match {
      case None => Future.successful(Ok(views.html.search.barcode(query)))
      case Some(s) =>
        barcodeDAO.findByCode(s).map {
          case Some(barcode) => Redirect(routes.Things.read(barcode.thingId)).flashing("success" -> "view.search.barcode.found")
          case None => Ok(views.html.search.barcode(query))
        }
    }
  }

  def barcodeRedir = Action { implicit request =>
    Redirect(routes.Search.barcode(Some(request.body.asFormUrlEncoded.get("query").head)))
  }
}
