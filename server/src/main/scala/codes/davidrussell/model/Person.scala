package codes.davidrussell.model

import play.api.libs.json.Json

object Person {
  implicit val writes = Json.writes[Person]
  implicit val reads = Json.reads[Person]
}

case class Person(id: Long, name: String, firstName: String, age: Long)


