package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

@Singleton
class Search @Inject()(
                        val thingDAO: ThingDAO,
                        val barcodeDAO: BarcodeDAO,
                        val labelDAO: LabelDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  def thing(query: String) = TODO

  def thingRedir = Action { implicit request =>
    Redirect(routes.Search.thing(request.body.asFormUrlEncoded.get("query").head))
  }

  def barcode(query: String) = TODO

  def barcodeRedir = Action { implicit request =>
    Redirect(routes.Search.barcode(request.body.asFormUrlEncoded.get("query").head))
  }

  def label(query: String) = TODO

  def labelRedir = Action { implicit request =>
    Redirect(routes.Search.label(request.body.asFormUrlEncoded.get("query").head))
  }
}
