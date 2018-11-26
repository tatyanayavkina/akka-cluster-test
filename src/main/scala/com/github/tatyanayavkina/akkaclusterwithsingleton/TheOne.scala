package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, SendMessage}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne._
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.RabbitSettings

class TheOne(instance: String, rabbitSettings: RabbitSettings) extends Actor with ActorLogging {

  val rabbitMQActor = context.actorOf(RabbitMQActor.props(rabbitSettings), "rabbit-mq-sender")

  override def receive: Receive = {
    case SendMessageToRabbit => sendMessage()
    case PoisonPill =>
      log.info("Get poison pill")
      rabbitMQActor ! End
      context.stop(self)
  }

  def sendMessage(): Unit = {
    val currentTime = LocalDateTime.now.format(formatter)
    val message = s"Send message to rabbit from $instance at ${currentTime}"
    log.info(message)
    rabbitMQActor ! SendMessage(message)
  }
}

object TheOne {
  case object SendMessageToRabbit
  case object EndProcess

  def props(instance: String, rabbitSettings: RabbitSettings): Props = Props(new TheOne(instance, rabbitSettings))

  import java.time.format.DateTimeFormatter

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
