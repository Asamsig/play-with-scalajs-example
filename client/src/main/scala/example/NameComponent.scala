package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.ext.Ajax
import play.api.libs.json.Json
import shared.Item

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

/**
  * Created by Alexander Samsig on 17-04-2017.
  */
object NameComponent {

  def Messages(str: String) = js.Dynamic.global.Messages(str).toString

  case class State(elements: Seq[String], page: Int)

  type NextPageClick = Int => Callback

  class Backend($ : BackendScope[Unit, State]) {

    def onChangePageClick(page: Int) = $.state flatMap { s =>
      Callback {
        $.modState(a => State(Seq.empty, 1)).runNow()

        val newPage = s.page + page

        val url = s"/myapi/list?page=$newPage"

        Ajax.get(url).foreach {
          case xhr =>
            if (xhr.status == 200) {
              val names = Json.parse(xhr.responseText).as[Seq[Item]]
              $.modState(a =>
                State(names.map(_.productIterator.mkString(" $")), 1))
                .runNow()
            }
        }
      }
    }

    def render(s: State) =
      nameList(s.elements, onChangePageClick)
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

  val name = ScalaComponent
    .builder[String]("Name")
    .render_P {
      case (p) =>
        tr(td(p))
    }
    .build

  val nameList = ScalaComponent
    .builder[(Seq[String], NextPageClick)]("NameList")
    .render_P {
      case (list, b) =>
        table(
          tableHead,
          tbody(
            if (list.isEmpty) {
              tr(
                td(colSpan := 10,
                   img(src := "https://ilt.taxmann.com/images/loading.gif",
                       height := "150px")))
            } else {
              list.toVdomArray(p => name.withKey(p)(p))
            }
          ),
          tableFooter(b)
        )
    }
    .build

  val NameApp = ScalaComponent
    .builder[Unit]("NameApp")
    .initialState(State(Nil, 1))
    .renderBackend[Backend]
    .componentDidMount(_.backend.onChangePageClick(1))
    .build

  def apply() = NameApp()
}
