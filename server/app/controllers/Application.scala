package controllers

import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._
import shared.{Item, SharedMessages}
import com.google.inject.Inject
import jsmessages.JsMessagesFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class Application @Inject()(
    val actorSystem: ActorSystem,
    jsMessagesFactory: JsMessagesFactory,
    mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc) {

  val items = Map(
    1 -> Seq(
      Item("apple", 108),
      Item("orange", 103),
      Item("banana", 1),
      Item("kiwi", 128)
    ),
    2 -> Seq(
      Item("pear", 108),
      Item("strawberry", 103),
      Item("cucumber", 6348),
      Item("onion", 187)
    )
  )

  def index = Action { implicit request =>
    Ok(views.html.index(SharedMessages.Loading))
  }

  def list(page: Option[Int] = None) = Action.async { implicit request =>
    val currentPage = page.filter(_ > 1).getOrElse(1)
    akka.pattern.after(1 seconds, actorSystem.scheduler) {
      Future.successful(Ok(Json.toJson(items(currentPage))))
    }
  }

  val messages = Action { implicit request =>
    Ok(jsMessagesFactory.all(Some("window.Messages"))).as("text/javascript")
  }

}
