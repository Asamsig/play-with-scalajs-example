package shared

import play.api.libs.json.Json

case class Item(name: String, description: String, price: BigDecimal)

object Item {
  implicit val itemFormat = Json.format[Item]
}