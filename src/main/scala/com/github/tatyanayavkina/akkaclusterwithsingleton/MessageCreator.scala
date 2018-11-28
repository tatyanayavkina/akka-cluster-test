package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MessageCreator {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  def createMessage(initiator: String, sender: String): String = {
    val currentTime = LocalDateTime.now.format(formatter)
    s"Send message to rabbit from $sender at $currentTime initiated by instance $initiator"
  }
}
