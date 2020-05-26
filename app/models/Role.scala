package models

import play.api.libs.json.{Json, OFormat}

case class Role(id: Long, name: String, description: String)

object Role {
  implicit val formatter: OFormat[Role] = Json.format[Role]
}