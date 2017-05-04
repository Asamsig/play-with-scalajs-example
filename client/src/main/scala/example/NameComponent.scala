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

    def onNextPageClick(page: Int) =
      $.state flatMap { s =>
        $.modState(_ => State(s.elements, 1))
      }

    def render(s: State) =
      nameList((s.elements, onNextPageClick))
  }

  val tableHead = ScalaComponent.static("TableHead")(thead(tr(td(Messages("name").capitalize))))()

  val name = ScalaComponent.builder[(String, NextPageClick)]("Name")
    .render_P {
      case (p, b) =>
        tr(td(p, onClick --> b(1)))
    }.build

  val nameList = ScalaComponent.builder[(List[String], NextPageClick)]("NameList")
    .render_P {
      case (list, b) =>
        table(
          tableHead,
          tbody(
          if (list.isEmpty)
            tr(td(colSpan := 10, img(src := "https://d2h8u6funiy290.cloudfront.net/wp-content/themes/youvisit/assets/img/form-loader.gif?x45988")))
          else
            list.toVdomArray(p => name((p, b)))
          )
        )
    }.build

  val NameApp = ScalaComponent.builder[Unit]("NameApp")
    .initialState(State(Nil, 1))
    .renderBackend[Backend]
    .componentDidMount(scope => Callback {

      val url =  js.Dynamic.global.Routes.controllers.Application.list().url.toString()

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