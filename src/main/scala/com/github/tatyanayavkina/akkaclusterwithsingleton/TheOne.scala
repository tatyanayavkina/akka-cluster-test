package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.SendMessage
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.{EndProcess, SendMessageToRabbit}

class TheOne(instance: String, rabbitMQActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case SendMessageToRabbit => sendMessage()
    case EndProcess =>
      log.info("Get poison pill")
      context.stop(self)
  }

  def sendMessage(): Unit = {
    log.info(s"Send message to rabbit from $instance")
    rabbitMQActor ! SendMessage(s"Send message to rabbit from $instance")
  }
}

object TheOne {
  case object SendMessageToRabbit
  case object EndProcess

  def props(instance: String, rabbitMQActor: ActorRef): Props = Props(new TheOne(instance, rabbitMQActor))
}
