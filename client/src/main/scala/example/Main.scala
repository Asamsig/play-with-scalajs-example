package example

import org.scalajs.dom

import scala.scalajs.js

object Main extends js.JSApp {

  def main(): Unit = {
    ItemComponent().renderIntoDOM(dom.document.getElementById("mydiv"))
  }

}