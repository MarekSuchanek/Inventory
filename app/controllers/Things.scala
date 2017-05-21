package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.Thing
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Things @Inject()(
                        val thingDAO: ThingDAO,
                        val labelDAO: LabelDAO,
                        val barcodeDAO: BarcodeDAO,
                        val messagesApi: MessagesApi
                      )
                      (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  val thingForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "thingType" -> nonEmptyText.verifying("model.thing.invalid.type", Thing.TYPES.contains(_)),
      "name" -> nonEmptyText,
      "description" -> text,
      "since" -> jodaDate("yyyy-MM-dd'T'HH:mm"),
      "until" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm"))
    )(Thing.apply)(Thing.unapply)
  )

  def index = Action.async { implicit request =>
    thingDAO.all().map { things => Ok(views.html.things.index(things)) }
  }

  def createForm(thing_type: Option[String]) = Action { implicit request =>
    thing_type match {
      case Some("atomic") => Ok(views.html.things.create(thingForm, Thing.ATOMIC_TYPE))
      case Some("container") => Ok(views.html.things.create(thingForm, Thing.CONTAINER_TYPE))
      case Some("functional_whole") => Ok(views.html.things.create(thingForm, Thing.FUNCTIONAL_TYPE))
      case _ => Redirect(routes.Things.index()).flashing("danger" -> "model.thing.invalid.type")
    }
  }

  def create = Action.async { implicit request =>
    thingForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.things.create(formWithErrors, formWithErrors.get.thingType)))
      },
      thing => {
        thingDAO.insert(thing).map(retId => Redirect(routes.Things.read(retId)).flashing("success" -> "view.thing.created"))
      }
    )
  }

  def read(id: Long) = Action.async { implicit request =>
    thingDAO.findById(id).map { result: Option[Thing] =>
      result match {
        case Some(thing) => Ok(views.html.things.read(thing, thingForm.fill(thing)))
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    thingForm.bindFromRequest.fold(
      formWithErrors => {
        thingDAO.findById(id).map { result: Option[Thing] =>
          result match {
            case Some(oldThing) => BadRequest(views.html.things.read(oldThing, formWithErrors))
            case None => NotFound
          }
        }
      },
      newThing => {
        thingDAO.update(id, newThing).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.updated"))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    thingDAO.delete(id).map { x: Int =>
      if (x == 1) Redirect(routes.Things.index()).flashing("success" -> "view.thing.deleted")
      else NotFound
    }
  }

  def linkLabel(id: Long) = TODO

  def unlinkLabel(id: Long) = TODO

  def addPart(id: Long) = TODO

  def removePart(id: Long) = TODO

}
