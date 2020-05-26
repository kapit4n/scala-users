package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String, roleId: Long, login: String, password: String)

object User {
  implicit val formatter: OFormat[User] = Json.format[User]
}

case class UserInfo(id: Long, firstName: String, lastName: String, roleName: String, roleId: Long)

object UserInfo {
  implicit val formatter: OFormat[UserInfo] = Json.format[UserInfo]
}

