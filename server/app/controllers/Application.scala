package controllers

import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._
import shared.{Item, SharedMessages}
import bab.{Item => ItemFormat}
import com.google.inject.Inject
import jsmessages.JsMessagesFactory
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._

class Application @Inject()(val actorSystem: ActorSystem, val messagesApi: MessagesApi, jsMessagesFactory: JsMessagesFactory)(implicit wja: WebJarAssets) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.index(SharedMessages.Loading))
  }

  def list = Action.async { implicit request =>
    akka.pattern.after(4 seconds, actorSystem.scheduler) {
      Future.successful(Ok(Json.toJson(Seq(
        Item("apple", 108),
        Item("orange", 103),
        Item("banana", 1),
        Item("kiwi", 128),
        Item("cucumber", 6348),
        Item("babned", 187)
      ).map(ItemFormat.itemFormat.writes)))
      )
    }
  }

  val messages = Action { implicit request =>
    Ok(jsMessagesFactory.all(Some("window.Messages"))).as("text/javascript")
  }

}
