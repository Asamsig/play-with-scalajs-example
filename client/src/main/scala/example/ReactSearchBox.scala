package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scalacss.ScalaCssReact._

object ReactSearchBox {

//  val cssSettings = scalacss.devOrProdDefaults
//  import cssSettings._

//  class Style extends StyleSheet.Inline {
//
//    import dsl._
//
//    val searchBox = style(marginBottom(10 px))
//
//    val input = style(
//      fontSize(13 px),
//      fontWeight._300,
//      padding(3 px),
//      width(100.%%),
//      backgroundColor.transparent,
//      borderBottom :=! "1px solid #B2ADAD",
//      &.focus.apply(outline.none, borderBottom :=! "1.5px solid #03a9f4")
//    )
//  }

  class Backend(t: BackendScope[Props, _]) {
    def onTextChange(P: Props)(e: ReactEventFromInput) =
      e.preventDefaultCB >> P.onTextChange(e.target.value)

    def render(P: Props) =
      <.div(^.cls := "mdl-textfield mdl-js-textfield mdl-textfield--expandable")(
        <.label(^.cls := "mdl-button mdl-js-button mdl-button--icon", ^.htmlFor := "searchInput")(
          <.i(^.cls := "material-icons", ^.color := "#757575")("search")
        ),
        <.div(^.cls := "mdl-textfield__expandable-holder")(
          <.input(
            ^.cls := "mdl-textfield__input",
            ^.id := "searchInput",
            ^.`type` := "text",
            ^.onKeyUp ==> onTextChange(P)
          ),
          <.label(^.cls := "mdl-textfield__label", ^.htmlFor := "sample-expandable")(
            "Expandable Input"
          )
        )
      )

//    <div class="mdl-textfield mdl-js-textfield mdl-textfield--expandable is-upgraded" data-upgraded=",MaterialTextfield">
//        <label class="mdl-button mdl-js-button mdl-button--icon" for="search" data-upgraded=",MaterialButton">
//          <i class="material-icons">search</i>
//        </label>
//        <div class="mdl-textfield__expandable-holder">
//          <input class="mdl-textfield__input" type="text" id="search">
//          <label class="mdl-textfield__label" for="search">Enter your query...</label>
//        </div>
//      </div>

//    <div class="mdl-textfield mdl-js-textfield mdl-textfield--expandable">
//        <label class="mdl-button mdl-js-button mdl-button--icon" for="sample6">
//          <i class="material-icons">search</i>
//        </label>
//        <div class="mdl-textfield__expandable-holder">
//          <input class="mdl-textfield__input" type="text" id="sample6">
//          <label class="mdl-textfield__label" for="sample-expandable">Expandable Input</label>
//        </div>
//    </div>
  }

//  object DefaultStyle extends Style

  val component = ScalaComponent
    .builder[Props]("ReactSearchBox")
    .stateless
    .renderBackend[Backend]
    .build

  case class Props(onTextChange: String => Callback)

  def apply(onTextChange: String => Callback) =
    component(Props(onTextChange))

}
