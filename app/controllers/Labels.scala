package controllers

import dao.LabelDAO
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.Label
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.MessagesApi

import scala.concurrent.{Future, ExecutionContext}

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

  def index = Action.async { implicit request =>
    labelDAO.all().map { labels => Ok(views.html.labels.index(labels)) }
  }

  def createForm = Action { implicit request =>
    Ok(views.html.labels.create(labelForm))
  }

  def create = Action.async { implicit request =>
    labelForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.labels.create(formWithErrors)))
      },
      label => {
        labelDAO.insert(label).map(retId => Redirect(routes.Labels.read(retId)).flashing("success" -> "view.label.created"))
      }
    )
  }

  def read(id: Long) = Action.async { implicit request =>
    labelDAO.findById(id).map { result: Option[Label] =>
      result match {
        case Some(label) => Ok(views.html.labels.read(label, labelForm.fill(label)))
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    labelForm.bindFromRequest.fold(
      formWithErrors => {
        labelDAO.findById(id).map { result: Option[Label] =>
          result match {
            case Some(oldLabel) => BadRequest(views.html.labels.read(oldLabel, formWithErrors))
            case None => NotFound
          }
        }
      },
      newLabel => {
        labelDAO.update(id, newLabel).map(_ => Redirect(routes.Labels.read(id)).flashing("success" -> "view.label.updated"))
      }
    )

  }

  def delete(id: Long) = Action.async { implicit request =>
    labelDAO.delete(id).map { x: Int =>
      if (x == 1) Redirect(routes.Labels.index()).flashing("success" -> "view.label.deleted")
      else NotFound
    }
  }

}
