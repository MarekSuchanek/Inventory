package controllers

import dao.LabelDAO
import play.api._
import play.api.mvc._
import javax.inject.Inject

import models.Label
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.ExecutionContext

class Application @Inject() (
                              labelDAO: LabelDAO
)(implicit executionContext: ExecutionContext) extends Controller {

  def index = Action.async {
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
