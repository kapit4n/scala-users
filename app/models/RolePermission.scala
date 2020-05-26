package models

import play.api.libs.json.{Json, OFormat}

case class RolePermission(id: Long, roleId: Long, permissionId: Long)

object RolePermission {
  implicit val formatter: OFormat[RolePermission] = Json.format[RolePermission]
}