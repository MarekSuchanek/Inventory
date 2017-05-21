package controllers

import dao.LabelDAO
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.Label
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

@Singleton
class Application @Inject()(
                             val labelDAO: LabelDAO,
                             val messagesApi: MessagesApi
                           )
                           (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {


  def index = Action { implicit request =>
    Ok(views.html.index())
  }
}
