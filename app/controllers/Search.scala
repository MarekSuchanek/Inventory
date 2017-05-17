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

  def thing(query: Option[String]) = TODO

  def thingRedir = Action { implicit request =>
    Redirect(routes.Search.thing(Some(request.body.asFormUrlEncoded.get("query").head)))
  }

  def barcode(query: Option[String]) = TODO

  def barcodeRedir = Action { implicit request =>
    Redirect(routes.Search.barcode(Some(request.body.asFormUrlEncoded.get("query").head)))
  }

  def label(query: Option[String]) = TODO

  def labelRedir = Action { implicit request =>
    Redirect(routes.Search.label(Some(request.body.asFormUrlEncoded.get("query").head)))
  }
}
