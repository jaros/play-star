package models

import java.time.LocalDate

import play.api.libs.json.Json

case class Envelope(message: String, date: LocalDate = LocalDate.now())

object JsonSerializers {
  implicit val envelopeWrites = Json.writes[Envelope]
}
