package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.{Barcode, Label, ThingRelation}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Relations @Inject()(
                          val thingDAO: ThingDAO,
                          val messagesApi: MessagesApi
                        )
                        (implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {

  val relationForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "relationType" -> nonEmptyText.verifying("model.relation.invalid.type", ThingRelation.TYPES.contains(_)),
      "partId" -> longNumber,
      "wholeId" -> longNumber,
      "since" -> jodaDate("yyyy-MM-dd'T'HH:mm"),
      "until" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm")),
      "slot" -> text,
      "function" -> optional(text),
      "quantity" -> optional(number)
    )(ThingRelation.apply)(ThingRelation.unapply)
  )

  def editForm(id: Long) = Action.async { implicit request =>
    thingDAO.findRelationById(id).map {
      case Some(relation) => Ok(views.html.relations.edit(relation, relationForm.fill(relation)))
      case None => NotFound
    }
  }

  def edit(id: Long) = Action.async { implicit request =>
    relationForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Relations.editForm(id)).flashing("danger" -> "view.relation.not_updated"))
      },
      relation => {
        thingDAO.updateRelation(id, relation).map(_ => Redirect(routes.Relations.editForm(id)).flashing("success" -> "view.relation.updated"))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    thingDAO.delete(id).map { x: Int =>
      if (x == 1) Redirect(routes.Things.index()).flashing("success" -> "view.relation.deleted")
      else NotFound
    }
  }

}
