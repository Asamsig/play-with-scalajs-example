package bab

import play.api.libs.json.Json

/**
  * Created by Korebian on 18-04-2017.
  */
object Item {
  implicit val itemFormat = Json.format[shared.Item]
}
