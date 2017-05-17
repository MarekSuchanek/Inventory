package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

@Singleton
class Barcodes @Inject()(
                        val barcodeDAO: BarcodeDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  def index = TODO

  def create = TODO

  def read(id: Long) = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO

}
