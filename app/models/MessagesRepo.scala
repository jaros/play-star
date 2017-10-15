package models

import models.Models.Message

object MessagesRepo {

  def all: List[Message] = List(
    Message("Hällo Döcker Würld"),
    Message("Blah bla"),
    Message("Ops"),
    Message("Döner")
  )

}
