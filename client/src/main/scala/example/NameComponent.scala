package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.ext.Ajax
import shared.Item

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSON

/**
  * Created by Alexander Samsig on 17-04-2017.
  */
object NameComponent {

  def Messages(str: String) = js.Dynamic.global.Messages(str).toString

  case class State(elements: List[String], page: Int)

  type NextPageClick = Int => Callback

  class Backend($: BackendScope[Unit, State]) {

    def onChangePageClick(page: Int) = $.state flatMap { s =>
      Callback {
        $.modState(a => State(List.empty, 1)).runNow()

        val newPage = s.page + page

        val url = js.Dynamic.global.Routes.controllers.Application.list(newPage).url.toString()

        Ajax.get(url).onSuccess {
          case xhr =>
            if (xhr.status == 200) {
              val data = JSON.parse(xhr.responseText).asInstanceOf[js.Array[js.Dynamic]]
              val names = data.toList.map(item => Item(item.name.toString, BigDecimal(item.price.asInstanceOf[Int])))
              $.modState(a => State(names.map(x => x.name + " " + x.price), 1)).runNow()
            }
        }
      }
    }

    def render(s: State) =
      nameList(s.elements, onChangePageClick)
  }

  val tableHead = ScalaComponent.static("TableHead")(thead(tr(td(Messages("name").capitalize))))()

  val tableFooter = ScalaComponent.builder[NextPageClick]("TableFooter").render_P { nextPage => tfoot(tr(td(ul(`class` := "pagination text-center", role := "navigation", li(`class` := "pagination-previous", a(onClick --> nextPage(-1), Messages("previous").capitalize)), li(`class` := "pagination-next", a(onClick --> nextPage(1), Messages("next").capitalize)))))) }.build

  val name = ScalaComponent.builder[String]("Name")
    .render_P {
      case (p) =>
        tr(td(p))
    }.build

  val nameList = ScalaComponent.builder[(List[String], NextPageClick)]("NameList")
    .render_P {
      case (list, b) =>
        table(
          tableHead,
          tbody(
            if (list.isEmpty)
              tr(td(colSpan := 10, img(src := "https://ilt.taxmann.com/images/loading.gif", height := "150px")))
            else
              list.toVdomArray(p => name.withKey(p)(p))
          ),
          tableFooter(b)
        )
    }.build

  val NameApp = ScalaComponent.builder[Unit]("NameApp")
    .initialState(State(Nil, 1))
    .renderBackend[Backend]
    .componentDidMount(scope => Callback {

      val url = js.Dynamic.global.Routes.controllers.Application.list().url.toString()

      Ajax.get(url).onSuccess {
        case xhr =>
          if (xhr.status == 200) {
            val data = JSON.parse(xhr.responseText).asInstanceOf[js.Array[js.Dynamic]]
            val names = data.toList.map(item => Item(item.name.toString, BigDecimal(item.price.asInstanceOf[Int])))
            scope.modState(_ => State(names.map(x => x.name + " " + x.price), 1)).runNow()
          }
      }
    }).build

  def apply() = NameApp()
}