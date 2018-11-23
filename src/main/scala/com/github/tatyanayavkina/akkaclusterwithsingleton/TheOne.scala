package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, SendMessage}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne._

class TheOne(instance: String, rabbitMQActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case SendMessageToRabbit => sendMessage()
    case EndProcess =>
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

  def props(instance: String, rabbitMQActor: ActorRef): Props = Props(new TheOne(instance, rabbitMQActor))

  import java.time.format.DateTimeFormatter

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
