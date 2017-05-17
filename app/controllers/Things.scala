package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

@Singleton
class Things @Inject()(
                        val thingDAO: ThingDAO,
                        val labelDAO: LabelDAO,
                        val barcodeDAO: BarcodeDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  def index = TODO

  def createForm(thing_type: Option[String]) = TODO

  def create = TODO

  def read(id: Long) = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO

  def linkLabel(id: Long) = TODO

  def unlinkLabel(id: Long) = TODO

  def addPart(id: Long) = TODO

  def removePart(id: Long) = TODO

}
