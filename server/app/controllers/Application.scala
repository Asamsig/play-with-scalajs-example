package controllers

import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._
import shared.Item
import com.google.inject.Inject
import data.Fruits
import jsmessages.JsMessagesFactory

import scala.concurrent.ExecutionContext

class Application @Inject()(val actorSystem: ActorSystem,
                            jsMessagesFactory: JsMessagesFactory,
                            mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc) {

  def index = Action { implicit request =>
    val scriptUrl = bundleUrl("client")
    Ok(views.html.main("Fancy pancy", scriptUrl))
  }

  val r = new scala.util.Random()

  def list() = Action { implicit request =>
    Ok(Json.toJson(Seq.fill(10)(randomItem)))
  }

  private def randomItem = {
    Item(Fruits.fruits(r.nextInt(Fruits.fruits.length)), "A type of fruit", r.nextInt(100))
  }

  val messages = Action { implicit request =>
    Ok(jsMessagesFactory.all(Some("window.Messages"))).as("text/javascript")
  }

  def bundleUrl(projectName: String): Option[String] = {
    val name = projectName.toLowerCase
    Seq(s"$name-opt-bundle.js", s"$name-fastopt-bundle.js")
      .find(name => getClass.getResource(s"/public/$name") != null)
      .map(controllers.routes.Assets.versioned(_).url)
  }

}
