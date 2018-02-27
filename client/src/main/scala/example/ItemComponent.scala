package example

import example.ReactTable.ColumnConfig
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
import ReactTable._

import scalacss.ProdDefaults._

/**
  * Created by Alexander Samsig on 17-04-2017.
  */
object ItemComponent {

  def Messages(str: String) = js.Dynamic.global.Messages(str).toString

  val configs = List(
    SimpleStringConfig[Item](name = "Name", _.name),
    SimpleStringConfig[Item](name = "Description", _.description),
    ColumnConfig[Item](name = "Price", item => item.price.toString(), numeric = true)(DefaultOrdering(_.price))
  )

  object MdlTableStyle {

    val Style = new ReactTable.Style {

      import dsl._

      override val table =
        style(addClassNames("mdl-data-table", "mdl-js-data-table", "mdl-shadow--2dp"), width :=! "100%")

    }
  }

  case class State(items: Seq[Item])

  case class Backend($ : BackendScope[Unit, State]) {

    Ajax.get("/myapi/list").map { xhr =>
      if (xhr.status == 200) {
        val items = Json.parse(xhr.responseText).as[Seq[Item]]
        $.setState(State(items)).runNow()
      }
    }

    def render(state: State) = {
      div(
        ReactTable(
          state.items,
          configs,
          style = MdlTableStyle.Style
        )(),
        div(cls := "mdl-spinner mdl-js-spinner is-active").when(state.items.isEmpty)
      )
    }
  }

  val ItemsApp = ScalaComponent
    .builder[Unit]("plain")
    .initialState(State(Seq()))
    .renderBackend[Backend]
    .build

  def apply() = ItemsApp()
}
