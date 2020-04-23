package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String, roleId: Long)

object User {
  implicit val formatter: OFormat[User] = Json.format[User]
}