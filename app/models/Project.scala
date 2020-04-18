package models

import play.api.libs.json.{Json, OFormat}

case class Project(name: String, description: String, cost: Int)

object Project {
  implicit val formatter: OFormat[Project] = Json.format[Project]
}