package example

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.{Flow, Sink, Source}
import example.ws.ItemsSource
import japgolly.scalajs.react.{Callback, _}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._
import play.api.libs.json.Json
import shared.Item

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

/**
  * Created by Alexander Samsig on 17-04-2017.
  */
object ItemComponent {

  def Messages(str: String) = js.Dynamic.global.Messages(str).toString

  case class State(elements: Seq[Item], page: Int)

  type NextPageClick = Int => Callback

  class Backend($ : BackendScope[Unit, State]) {

    implicit lazy val system       = ActorSystem("webby")
    implicit lazy val materializer = ActorMaterializer()

    private lazy val stream = ItemsSource("localhost:9000/myapi/stream", 100 milliseconds)

    private lazy val soure = Source
      .fromGraph(stream)
      .mapAsync(10) { item =>
        $.modState(state => State(state.elements.+:(item), state.page)).async.runNow()
      }
      .to(Sink.ignore)
      .run()

    def onChangePageClick(page: Int) = $.state flatMap { s =>
      Callback {
        val newPage = s.page + page

        val url = s"/myapi/list?page=$newPage"

        Ajax.get(url).flatMap { xhr =>
          if (xhr.status == 200) {
            val items = Json.parse(xhr.responseText).as[Seq[Item]]
            $.setState(State(items, newPage)).async.runNow()
          } else {
            Future.successful(())
          }
        }

      }
    }

    def openStream() = Callback {
      stream.open()
      soure
    }

    def closeStream() = Callback {
      stream.closeConnection()
    }

    def render(s: State) =
      itemList(s.elements, onChangePageClick, openStream, closeStream)
  }

  val tableHead = ScalaComponent.static("TableHead")(thead(tr(td(Messages("name").capitalize))))()

  val tableFooter = ScalaComponent
    .builder[NextPageClick]("TableFooter")
    .render_P { nextPage =>
      tfoot(
        tr(
          td(
            ul(
              `class` := "pagination text-center",
              role := "navigation",
              li(`class` := "pagination-previous", a(onClick --> nextPage(-1), Messages("previous").capitalize)),
              li(`class` := "pagination-next", a(onClick --> nextPage(1), Messages("next").capitalize))
            )
          )
        )
      )
    }
    .build

  val item = ScalaComponent
    .builder[Item]("Item")
    .render_P {
      case (p) =>
        tr(td(p.productIterator.mkString(" $")))
    }
    .build

  val itemList = ScalaComponent
    .builder[(Seq[Item], NextPageClick, Callback, Callback)]("NameList")
    .render_P {
      case (list, b, stream, close) =>
        div(
          button(`class` := "button", onClick --> stream, "STREAM"),
          button(`class` := "button", onClick --> close, "CLOSE"),
          table(
            tableHead,
            tbody(
              if (list.isEmpty) {
                loader
              } else {
                list.toVdomArray(p => item.withKey(p.name)(p))
              }
            ),
            tableFooter(b)
          )
        )
    }
    .build

  val loader = {
    ScalaComponent.static("loader")(
      tr(td(colSpan := 10, img(src := "https://ilt.taxmann.com/images/loading.gif", height := "150px")))
    )()
  }

  val ItemsApp = ScalaComponent
    .builder[Unit]("ItemsApp")
    .initialState(State(Nil, 1))
    .renderBackend[Backend]
    .componentDidMount(_.backend.onChangePageClick(0))
    .build

  def apply() = ItemsApp()
}
