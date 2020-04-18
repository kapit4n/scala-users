package models

import play.api.libs.json.{Json, OFormat}

case class Task(name: String, estimated: Int, tracked: Int)

object Task {
  implicit val formatter: OFormat[Task] = Json.format[Task]
}