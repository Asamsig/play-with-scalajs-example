package example

import org.scalajs.dom

import scala.scalajs.js

object ScalaJSExample extends js.JSApp {

  def main(): Unit = {
    NameComponent().renderIntoDOM(dom.document.getElementById("mydiv"))
  }

}