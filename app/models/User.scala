package models

import play.api.libs.json.{Json, OFormat}

case class User(firstName: String, lastName: String, age: Int, capacity: Int)

object User {
  implicit val formatter: OFormat[User] = Json.format[User]
}