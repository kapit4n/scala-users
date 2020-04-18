package models

import play.api.libs.json.{Json, OFormat}

case class AppInfo(name: String, version: String)

object AppInfo {
  implicit val formatter: OFormat[AppInfo] = Json.format[AppInfo]
}