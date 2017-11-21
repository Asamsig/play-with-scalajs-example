package controllers

import akka.actor.ActorSystem
import akka.stream.ThrottleMode
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json._
import play.api.mvc._
import shared.{Item, SharedMessages}
import com.google.inject.Inject
import data.Fruits
import jsmessages.JsMessagesFactory

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.mvc.WebSocket.MessageFlowTransformer

class Application @Inject()(
    val actorSystem: ActorSystem,
    jsMessagesFactory: JsMessagesFactory,
    mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc) {

  val r = new scala.util.Random(1000)

  val items = mutable.Map(
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

  implicit val messageFlowTransformer =
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Item]

  def stream = WebSocket.accept[String, Item] { implicit request =>
    val in = Sink.foreach[String](println)

    val out = Source(Stream.continually(randomItem))
      .throttle(200, 1 second, 5, _.price.toInt, ThrottleMode.Shaping)

    Flow.fromSinkAndSource(in, out)
  }

  private def randomItem = {
    Item(Fruits.fruits(r.nextInt(Fruits.fruits.length)), r.nextInt(100))
  }

  val messages = Action { implicit request =>
    Ok(jsMessagesFactory.all(Some("window.Messages"))).as("text/javascript")
  }

}
