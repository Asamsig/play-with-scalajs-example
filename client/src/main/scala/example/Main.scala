package example

import example.ItemComponent.MdlTableStyle
import org.scalajs.dom

import scalacss.internal.mutable.GlobalRegistry
import scalacss.DevDefaults._
import scalacss.ScalaCssReact._
object Main {

  def main(args: Array[String]): Unit = {
    GlobalRegistry.register(
//      ReactTable.DefaultStyle,
//      ReactListView.DefaultStyle,
//      ReactSearchBox.DefaultStyle,
//      Pager.DefaultStyle,
//      ReactDraggable.Style
      MdlTableStyle.Style
    )
    GlobalRegistry.addToDocumentOnRegistration()

    ItemComponent().renderIntoDOM(dom.document.getElementById("entry-point"))
  }

}
