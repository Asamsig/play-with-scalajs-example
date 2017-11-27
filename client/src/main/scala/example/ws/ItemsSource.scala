package example.ws

import akka.NotUsed
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, OutHandler, TimerGraphStageLogic}
import org.scalajs.dom._
import play.api.libs.json.Json
import shared.Item

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

case class ItemsSource(websocketUrl: String, timeout: FiniteDuration)
    extends GraphStage[SourceShape[Item]] {

  val out: Outlet[Item]                 = Outlet("ItemsSource")
  override val shape: SourceShape[Item] = SourceShape(out)
  private var ws: WebSocket             = _
  private val buffer                    = mutable.Queue.empty[Item]

  def closeConnection() = {
    if (ws != null) {
      ws.close()
      ws = null
    }
  }

  def open() = {
    if (ws == null) {
      ws = new WebSocket(s"ws://$websocketUrl")
    }
    ws.onmessage = { (event: MessageEvent) =>
      buffer.enqueue(Json.parse(event.data.toString).as[Item])
    }
  }

  override def createLogic(inheritedAttributes: Attributes) = new TimerGraphStageLogic(shape) {

    override protected def onTimer(timerKey: Any) = retrieveMessages

    def retrieveMessages(): Unit = {
      val res = Try(buffer.dequeue()).toOption.toList

      if (res.isEmpty) {
        if (isAvailable(out)) {
          scheduleOnce(NotUsed, timeout)
        }
      } else {
        buffer ++= res
        emit(out, buffer.dequeue)
      }
    }

    override def postStop(): Unit = {
      super.postStop()
      ws.onclose = { (event: Event) ⇒
        println("connection was closed!")
      }
    }

    setHandler(
      out,
      new OutHandler {
        override def onPull(): Unit = {
          if (buffer.nonEmpty) {
            emit(out, buffer.dequeue)
          } else {
            retrieveMessages()
          }
        }
      }
    )

  }

}
