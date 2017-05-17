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


  def index = Action.async { implicit request =>
    labelDAO.all().map { labels => Ok(views.html.index(labels)) }
  }

  val thingForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> text,
      "description" -> text,
      "color" -> text
    )(Label.apply)(Label.unapply)
  )

  def insertLabel = Action.async { implicit request =>
    val label: Label = thingForm.bindFromRequest.get
    labelDAO.insert(label).map(_ => Redirect(routes.Application.index))
  }
}
