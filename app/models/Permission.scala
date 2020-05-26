package models

import play.api.libs.json.{Json, OFormat}

case class Permission(id: Long, name: String, obj: String, action: String)

object Permission {
  implicit val formatter: OFormat[Permission] = Json.format[Permission]
}