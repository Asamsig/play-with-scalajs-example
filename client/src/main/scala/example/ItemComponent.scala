package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._
import play.api.libs.json.Json
import shared.Item

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

    def onChangePageClick(page: Int) = $.state flatMap { s =>
      Callback {
        val newPage = s.page + page

        $.modState(a => State(Seq.empty, newPage)).runNow()

        val url = s"/myapi/list?page=$newPage"

        Ajax.get(url).foreach {
          case xhr =>
            if (xhr.status == 200) {
              val items = Json.parse(xhr.responseText).as[Seq[Item]]
              $.modState(a =>
                State(items, newPage))
                .runNow()
            }
        }

        val chat = new WebSocket("ws:localhost:9000/myapi/stream")
        chat.onopen = { (event: Event) ⇒
          println("connection was successful!")
        }

        chat.onerror = { (event: ErrorEvent) ⇒
        }

        chat.onmessage = { (event: MessageEvent) ⇒
          val wsMsg = Json.parse(event.data.toString).as[Item]

          println(wsMsg)

          $.modState(state => State(state.elements.+:(wsMsg), state.page)).runNow()
        }
        chat.onclose = { (event: Event) ⇒
          println("connection was closed!")
        }
      }
    }

    def render(s: State) =
      itemList(s.elements, onChangePageClick)
  }

  val tableHead = ScalaComponent.static("TableHead")(
    thead(tr(td(Messages("name").capitalize))))()

  val tableFooter = ScalaComponent
    .builder[NextPageClick]("TableFooter")
    .render_P { nextPage =>
      tfoot(
        tr(td(ul(
          `class` := "pagination text-center",
          role := "navigation",
          li(`class` := "pagination-previous",
             a(onClick --> nextPage(-1), Messages("previous").capitalize)),
          li(`class` := "pagination-next",
             a(onClick --> nextPage(1), Messages("next").capitalize))
        ))))
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
    .builder[(Seq[Item], NextPageClick)]("NameList")
    .render_P {
      case (list, b) =>
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
    }
    .build

  val loader = {
    ScalaComponent.static("loader")(
      tr(
        td(colSpan := 10,
           img(src := "https://ilt.taxmann.com/images/loading.gif",
               height := "150px"))))()
  }

  val ItemsApp = ScalaComponent
    .builder[Unit]("ItemsApp")
    .initialState(State(Nil, 1))
    .renderBackend[Backend]
    .componentDidMount(_.backend.onChangePageClick(0))
    .build

  def apply() = ItemsApp()
}
