package controllers

import javax.inject.{Inject, Singleton}

import dao.LabelDAO
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

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
