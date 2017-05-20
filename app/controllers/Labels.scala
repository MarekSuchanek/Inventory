package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.Label
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

@Singleton
class Labels @Inject()(
                        val labelDAO: LabelDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  val labelForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "description" -> text,
      "color" -> nonEmptyText.verifying(pattern("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$".r))
    )(Label.apply)(Label.unapply)
  )

  def index = TODO

  def createForm =  Action { implicit request =>
    Ok(views.html.label.create(labelForm))
  }

  def create = Action { implicit request =>
    labelForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.label.create(labelForm))
      },
      userData => {
        Redirect(routes.Application.index())
      }
    )
  }

  def read(id: Long) = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO

}
