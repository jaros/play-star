package models

import java.time.LocalDate

import models.Models.PageStatus.PageStatus
import play.api.libs.json.Json


object Models {

  object PageStatus extends Enumeration {
    type PageStatus = Value
    val Ok, Unauthorized, InternalServerError = Value
  }

  case class Message(text: String)

  case class Envelope(message: Map[String, Message], date: LocalDate = LocalDate.now())

  case class PageView(status: PageStatus, envelope: Envelope)

  implicit val messageWrites = Json.writes[Message]
  implicit val envelopeWrites = Json.writes[Envelope]
  implicit val pageViewWriter = Json.writes[PageView]
}

