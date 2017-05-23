package controllers

import dao.{BarcodeDAO, LabelDAO, ThingDAO}
import play.api._
import play.api.mvc._
import play.api.i18n.I18nSupport
import javax.inject.{Inject, Singleton}

import models.{Barcode, LabelThing, Thing, ThingRelation}
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

  val linkLabelForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "labelId" -> longNumber,
      "thingId" -> longNumber
    )(LabelThing.apply)(LabelThing.unapply)
  )

  val barcodeForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "standard" -> nonEmptyText,
      "code" -> nonEmptyText, //TODO: verify unique
      "thingId" -> longNumber
    )(Barcode.apply)(Barcode.unapply)
  )

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
    lazy val data = for {
      thing <- thingDAO.findById(id)
      linkedLabels <- labelDAO.getLinkedLabels(id)
      labelsToAdd <- labelDAO.all()
      barcodes <- barcodeDAO.getLinkedBarcodes(id)
      parts <- thingDAO.getParts(id)
      wholes <- thingDAO.getWholes(id)
      things <- thingDAO.all()
    } yield (thing, labelsToAdd, linkedLabels, barcodes, parts, wholes, things)

    data.map { case (xthing, labelsToAdd, linkedLabels, barcodes, parts, wholes, things) =>
      xthing match {
        case Some(thing) => Ok(
          views.html.things.read(
            thing,
            thingForm.fill(thing), linkLabelForm, barcodeForm, relationForm,
            labelsToAdd, linkedLabels, barcodes, parts, wholes, things
          )
        )
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    thingForm.bindFromRequest.fold(
      formWithErrors => {
        thingDAO.findById(id).map {
            case Some(oldThing) => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.not_updated")
            case None => NotFound
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

  def linkLabel(id: Long) = Action.async { implicit request =>
    linkLabelForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.label.not_linked"))
      },
      newLabelThing => {
        labelDAO.linkLabel(newLabelThing).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.label.linked"))
      }
    )
  }

  def unlinkLabel(id: Long) = Action.async { implicit request =>
    linkLabelForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.label.not_unlinked"))
      },
      oldLabelThing => {
        labelDAO.unlinkLabel(oldLabelThing.id.getOrElse(0)).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.label.unlinked"))
      }
    )
  }

  def addBarcode(id: Long) = Action.async { implicit request =>
    barcodeForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.barcode.not_added"))
      },
      newBarcode => {
        barcodeDAO.insert(newBarcode).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.barcode.added"))
      }
    )
  }

  def removeBarcode(id: Long) = Action.async { implicit request =>
    barcodeForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.barcode.not_removed"))
      },
      oldBarcode => {
        barcodeDAO.delete(oldBarcode.id.getOrElse(0)).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.barcode.removed"))
      }
    )
  }

  def addPart(id: Long) = Action.async { implicit request =>
    relationForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.part.not_added"))
      },
      newRelation => {
        thingDAO.insertRelation(newRelation).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.part.added"))
      }
    )
  }

  def removePart(id: Long) = Action.async { implicit request =>
    relationForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.Things.read(id)).flashing("danger" -> "view.thing.part.not_removed"))
      },
      oldRelation => {
        thingDAO.deleteRelation(oldRelation.id.getOrElse(0)).map(_ => Redirect(routes.Things.read(id)).flashing("success" -> "view.thing.part.removed"))
      }
    )
  }

}
