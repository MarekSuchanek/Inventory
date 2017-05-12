package controllers

import dao.ThingDAO
import play.api._
import play.api.mvc._
import javax.inject.Inject

import models.Thing
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.ExecutionContext

class Application @Inject() (
  thingDAO: ThingDAO
)(implicit executionContext: ExecutionContext) extends Controller {

  def index = Action.async {
    thingDAO.all().map { things => Ok(views.html.index(things)) }
  }

  val thingForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> text
    )(Thing.apply)(Thing.unapply)
  )

  def insertThing = Action.async { implicit request =>
    val thing: Thing = thingForm.bindFromRequest.get
    thingDAO.insert(thing).map(_ => Redirect(routes.Application.index))
  }
}
