package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, SendMessage}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne._
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.RabbitSettings

class TheOne(instance: String, rabbitSettings: RabbitSettings) extends Actor with ActorLogging {

  val rabbitMQActor = context.actorOf(RabbitMQActor.props(rabbitSettings), "rabbit-mq-sender")
  log.info(s"$instance started")

  override def receive: Receive = {
    case SendMessageToRabbit(from) => sendMessage(from)
    case PoisonPill =>
      log.info(s"$instance got poison pill")
      rabbitMQActor ! End
      context.stop(self)
  }

  def sendMessage(from: String): Unit = {
    val currentTime = LocalDateTime.now.format(formatter)
    val message = s"Send message to rabbit from $instance at $currentTime initiated by instance $from"
    log.info(message)
    rabbitMQActor ! SendMessage(message)
  }
}

object TheOne {
  case class SendMessageToRabbit(from: String)
  case object EndProcess

  def props(instance: String, rabbitSettings: RabbitSettings): Props = Props(new TheOne(instance, rabbitSettings))

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
