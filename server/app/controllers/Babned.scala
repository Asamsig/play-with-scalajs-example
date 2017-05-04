package controllers

import akka.actor.ActorSystem
import com.google.inject.Inject
import jsmessages.JsMessagesFactory
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import shared.SharedMessages

class Babned @Inject()(val actorSystem: ActorSystem, val messagesApi: MessagesApi, jsMessagesFactory: JsMessagesFactory)(implicit wja: WebJarAssets) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.index(SharedMessages.Loading))
  }

}
