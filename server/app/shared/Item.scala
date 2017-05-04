package bab

import play.api.libs.json.Json

/**
  * Created by Alexander Samsig on 18-04-2017.
  */
object Item {
  implicit val itemFormat = Json.format[shared.Item]
}
